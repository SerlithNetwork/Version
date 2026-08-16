package net.serlith.version.server.manager;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.security.authentication.KeyAuthenticationToken;
import org.jspecify.annotations.NullMarked;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@NullMarked
@RequiredArgsConstructor
public class JwtAuthenticationConverter implements ServerAuthenticationConverter {

    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return Mono.just(exchange)
                .flatMap(i -> {

                    String header = i.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
                    if (header == null || header.isBlank() || !header.startsWith(BEARER_PREFIX)) {
                        return Mono.empty();
                    }

                    String token = header.substring(BEARER_PREFIX.length());
                    if (token.isBlank()) {
                        return Mono.empty();
                    }

                    return Mono.just(new KeyAuthenticationToken(token));
                });
    }

}
