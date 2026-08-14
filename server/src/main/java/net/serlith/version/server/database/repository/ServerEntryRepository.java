package net.serlith.version.server.database.repository;

import net.serlith.version.server.database.types.ServerEntry;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface ServerEntryRepository extends R2dbcRepository<ServerEntry, Long> {

    Flux<ServerEntry> findAllBySoftware(long software);

}
