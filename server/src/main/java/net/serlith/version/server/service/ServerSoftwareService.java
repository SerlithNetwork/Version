package net.serlith.version.server.service;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.schema.Tables;
import net.serlith.version.server.schema.tables.records.VersionSoftwareRecord;
import net.serlith.version.server.schema.tables.records.VersionVersionRecord;
import net.serlith.version.server.types.*;
import net.serlith.version.server.types.software.SoftwareUpdateRequest;
import net.serlith.version.server.util.TokenUtils;
import org.jooq.DSLContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServerSoftwareService {

    private static final int TOKEN_KEY_LENGTH = 96;

    private final DSLContext dsl;
    private final PasswordEncoder encoder;

    public Flux<SoftwareDataTokenless> fetchAllServers() {
        return Flux.from(
                this.dsl.selectFrom(Tables.VERSION_SOFTWARE)
                        .orderBy(Tables.VERSION_SOFTWARE.ID)
        ).map(SoftwareDataTokenless::from);
    }

    public Mono<SoftwareDataTokenless> fetchServerFromToken(final String token) {
        return TokenUtils.parseToken(token)
                .flatMap(tuple -> {
                    long id = tuple.getT1();
                    return Mono.from(
                            this.dsl.selectFrom(Tables.VERSION_SOFTWARE)
                                    .where(Tables.VERSION_SOFTWARE.ID.eq(id))
                    );
                }).filter(software -> this.encoder.matches(token, software.getToken()))
                .map(SoftwareDataTokenless::from);
    }

    public Mono<VersionData> fetchServerData(final String software, final String version) {
        return Mono.from(
                this.dsl.select(Tables.VERSION_VERSION.fields())
                        .from(Tables.VERSION_VERSION)
                        .join(Tables.VERSION_SOFTWARE).on(Tables.VERSION_SOFTWARE.ID.eq(Tables.VERSION_VERSION.SOFTWARE_ID))
                        .where(Tables.VERSION_VERSION.VERSION.eq(version))
                        .and(Tables.VERSION_SOFTWARE.NAME.eq(software))
        )
                .map(record -> record.into(Tables.VERSION_VERSION))
                .map(VersionData::from);
    }

    @Transactional
    public Mono<SoftwareDataTokenized> createServer(final SoftwareUpdateRequest request) {
        final String key = TokenUtils.generateRandomKey(TOKEN_KEY_LENGTH);
        return Mono.from(
                this.dsl.insertInto(Tables.VERSION_SOFTWARE)
                        .set(Tables.VERSION_SOFTWARE.NAME, request.name())
                        .set(Tables.VERSION_SOFTWARE.DISPLAY, request.displayName())
                        .set(Tables.VERSION_SOFTWARE.TOKEN, key)
                        .returning()
        ).flatMap(result -> {
            final String token = TokenUtils.tokenFromIdAndKey(result.getId(), result.getToken());
            return Mono.zip(
                    Mono.just(token),
                    Mono.from(
                            this.dsl.update(Tables.VERSION_SOFTWARE)
                                    .set(Tables.VERSION_SOFTWARE.TOKEN, token)
                                    .where(Tables.VERSION_SOFTWARE.ID.eq(result.getId()))
                                    .returning()
                    )
            );
        }).map(tuple -> {
            final String token = tuple.getT1();
            final VersionSoftwareRecord record = tuple.getT2();
            return SoftwareDataTokenized.from(record, token);
        });
    }

    @Transactional
    public Mono<ServerVersionData> mergeServerBuild(final SubmitBuildRequest request) {
        return Mono.from(
                this.dsl.selectFrom(Tables.VERSION_SOFTWARE)
                        .where(Tables.VERSION_SOFTWARE.NAME.equalIgnoreCase(request.software()))
                )
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("Requested project '%s' doesn't exist", request.software()))))
                .flatMap(server -> Mono.zip(Mono.just(server), Flux.from(
                        this.dsl.selectFrom(Tables.VERSION_VERSION)
                                .where(Tables.VERSION_VERSION.SOFTWARE_ID.eq(server.getId()))
                ).collectList()))
                .flatMap(tuple -> {
                    final VersionSoftwareRecord software = tuple.getT1();
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

    public Mono<SoftwareDataTokenless> updateSoftware(long id, SoftwareUpdateRequest request) {
        return Mono.from(
                this.dsl.update(Tables.VERSION_SOFTWARE)
                        .set(Tables.VERSION_SOFTWARE.NAME, request.name())
                        .set(Tables.VERSION_SOFTWARE.DISPLAY, request.displayName())
                        .where(Tables.VERSION_SOFTWARE.ID.eq(id))
                        .returning()
        ).map(SoftwareDataTokenless::from);
    }

    public Mono<SoftwareDataTokenized> resetSoftwareToken(long id) {
        final String token = TokenUtils.generateRandomTokenFromId(id, TOKEN_KEY_LENGTH);
        return Mono.from(
                this.dsl.update(Tables.VERSION_SOFTWARE)
                        .set(Tables.VERSION_SOFTWARE.TOKEN, this.encoder.encode(token))
                        .where(Tables.VERSION_SOFTWARE.ID.eq(id))
                        .returning()
        ).map(result -> SoftwareDataTokenized.from(result, token));
    }

    public Mono<Integer> deleteSoftware(long id) {
        return Mono.from(
                this.dsl.deleteFrom(Tables.VERSION_SOFTWARE)
                        .where(Tables.VERSION_SOFTWARE.ID.eq(id))
        );
    }

}
