package net.serlith.version.server.service;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.database.repository.ServerEntryRepository;
import net.serlith.version.server.database.repository.ServerSoftwareRepository;
import net.serlith.version.server.database.types.ServerEntry;
import net.serlith.version.server.database.types.ServerSoftware;
import net.serlith.version.server.types.SubmitBuildRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ServerSoftwareService {

    private final ServerSoftwareRepository softwareRepository;
    private final ServerEntryRepository serverRepository;

    @Transactional
    public Mono<ServerEntry> mergeServerBuild(final SubmitBuildRequest request) {
        return this.softwareRepository.findByNameEqualsIgnoreCase(request.software())
                .switchIfEmpty(Mono.error(new IllegalStateException(String.format("Requested project '%s' doesn't exist", request.software()))))
                .flatMap(software -> Mono.zip(Mono.just(software), this.serverRepository.findAllBySoftware(software.id).collectList()))
                .flatMap(tuple -> {
                    final ServerSoftware software = tuple.getT1();
                    final List<ServerEntry> entries = tuple.getT2();
                    final Optional<ServerEntry> match = entries.stream().filter(entry -> entry.version.equalsIgnoreCase(request.version())).findFirst();
                    final ServerEntry current = match.orElseGet(() -> {
                        final ServerEntry entry = new ServerEntry();
                        entry.software = software.id;
                        entry.version = request.version();
                        entry.build = 0;
                        return entry;
                    });

                    if (current.build >= request.build()) {
                        return Mono.error(new IllegalStateException(String.format("Submitted invalid build. Existing (%d) >= Requested (%d)", current.build, request.build())));
                    }
                    current.build = request.build();

                    return this.serverRepository.save(current);
                });
    }

}
