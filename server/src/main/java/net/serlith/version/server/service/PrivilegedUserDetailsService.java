package net.serlith.version.server.service;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.schema.Tables;
import org.jooq.DSLContext;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@NullMarked
@RequiredArgsConstructor
public class PrivilegedUserDetailsService implements ReactiveUserDetailsService {

    private final DSLContext dsl;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        return Mono.from(
                this.dsl.selectFrom(Tables.VERSION_PRIVILEGED_USER)
                        .where(Tables.VERSION_PRIVILEGED_USER.USERNAME.eq(username))
        ).map(user -> User.withUsername(user.getUsername())
                .password(user.getPassword())
                .build()
        );
    }

}
