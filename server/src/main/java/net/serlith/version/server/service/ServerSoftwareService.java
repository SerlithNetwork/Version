package net.serlith.version.server.service;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.schema.Tables;
import net.serlith.version.server.schema.tables.records.VersionServerRecord;
import net.serlith.version.server.schema.tables.records.VersionVersionRecord;
import net.serlith.version.server.types.ServerVersionData;
import net.serlith.version.server.types.SubmitBuildRequest;
import net.serlith.version.server.types.VersionData;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServerSoftwareService {

    private final DSLContext dsl;

    public Mono<VersionData> fetchServerData(final String software, final String version) {
        return Mono.from(
                this.dsl.select(Tables.VERSION_VERSION.fields())
                        .from(Tables.VERSION_VERSION)
                        .join(Tables.VERSION_SERVER).on(Tables.VERSION_SERVER.ID.eq(Tables.VERSION_VERSION.SOFTWARE_ID))
                        .where(Tables.VERSION_VERSION.VERSION.eq(version))
                        .and(Tables.VERSION_SERVER.NAME.eq(software))
        )
                .map(record -> record.into(Tables.VERSION_VERSION))
                .map(VersionData::from);
    }

    @Transactional
    public Mono<ServerVersionData> mergeServerBuild(final SubmitBuildRequest request) {
        return Mono.from(
                this.dsl.selectFrom(Tables.VERSION_SERVER)
                        .where(Tables.VERSION_SERVER.NAME.equalIgnoreCase(request.software()))
                )
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("Requested project '%s' doesn't exist", request.software()))))
                .flatMap(server -> Mono.zip(Mono.just(server), Flux.from(
                        this.dsl.selectFrom(Tables.VERSION_VERSION)
                                .where(Tables.VERSION_VERSION.SOFTWARE_ID.eq(server.getId()))
                ).collectList()))
                .flatMap(tuple -> {
                    final VersionServerRecord software = tuple.getT1();
                    final List<VersionVersionRecord> entries = tuple.getT2();
                    final Optional<VersionVersionRecord> match = entries.stream().filter(entry -> entry.getVersion().equalsIgnoreCase(request.version())).findFirst();
                    final long current = match.stream().mapToLong(VersionVersionRecord::getBuild).findFirst().orElse(0L);
                    if (current >= request.build()) {
                        return Mono.error(new IllegalStateException(String.format("Submitted invalid build. Existing (%d) >= Requested (%d)", current, request.build())));
                    }
                    return Mono.zip(
                            Mono.just(software),
                            Mono.from(
                                    this.dsl.insertInto(Tables.VERSION_VERSION)
                                            .set(Tables.VERSION_VERSION.SOFTWARE_ID, software.getId())
                                            .set(Tables.VERSION_VERSION.VERSION, request.version())
                                            .set(Tables.VERSION_VERSION.BUILD, request.build())
                                            .onDuplicateKeyUpdate()
                                            .set(Tables.VERSION_VERSION.BUILD, request.build())
                                            .returning()
                            )
                    );
                }).map(ServerVersionData::from);
    }

}
