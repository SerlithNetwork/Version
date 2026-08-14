package net.serlith.version.server.manager;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.security.authentication.KeyAuthenticationToken;
import net.serlith.version.server.service.ServerSoftwareService;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@NullMarked
@RequiredArgsConstructor
public class CiAuthenticationManager implements ReactiveAuthenticationManager {

    private final ServerSoftwareService service;

    @Override
    public Mono<Authentication> authenticate(final Authentication authentication) {
        if (!(authentication instanceof KeyAuthenticationToken keyAuthentication)) {
            return Mono.empty();
        }

        String token = keyAuthentication.getCredentials();
        return this.service.fetchServerFromToken(token)
                .flatMap(software -> Mono.just((Authentication) new KeyAuthenticationToken(software.name(), token)))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid API Token")));
    }

}
