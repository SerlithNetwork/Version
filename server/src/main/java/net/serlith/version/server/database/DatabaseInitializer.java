package net.serlith.version.server.database;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.schema.Tables;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@NullMarked
@RequiredArgsConstructor
public class DatabaseInitializer implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseInitializer.class);

    @Value("${version.manager.username}")
    private String username;

    @Value("${version.manager.password}")
    private String password;

    private final DSLContext dsl;
    private final PasswordEncoder encoder;

    @Override
    public void run(ApplicationArguments args) {
        Flux.from(this.dsl.selectFrom(Tables.VERSION_PRIVILEGED_USER))
                .collectList()
                .filter(List::isEmpty)
                .flatMap(ignore -> {
                    LOGGER.info("Initializing default privileged user...");
                    return Mono.from(
                            this.dsl.insertInto(Tables.VERSION_PRIVILEGED_USER)
                                    .set(Tables.VERSION_PRIVILEGED_USER.USERNAME, this.username)
                                    .set(Tables.VERSION_PRIVILEGED_USER.PASSWORD, this.encoder.encode(this.password))
                                    .returning()
                    ).doOnSuccess(user -> {
                        if (user == null) {
                            throw new IllegalStateException("Failed to create privileged user");
                        }
                        LOGGER.info("Successfully initialized [{}] as privileged", user.getUsername());
                    });
                }).subscribe();
    }
}
