package net.serlith.version.server.database.repository;

import net.serlith.version.server.database.types.ServerSoftware;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ServerSoftwareRepository extends R2dbcRepository<ServerSoftware, Long> {

    Mono<ServerSoftware> findByNameEqualsIgnoreCase(String name);

}
