package net.serlith.version.server.manager;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.security.authentication.KeyAuthenticationToken;
import org.jspecify.annotations.NullMarked;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@NullMarked
@RequiredArgsConstructor
public class CiAuthenticationConverter implements ServerAuthenticationConverter {

    private static final Logger LOGGER = LoggerFactory.getLogger(CiAuthenticationConverter.class);

    @Override
    public Mono<Authentication> convert(final ServerWebExchange exchange) {
        HttpHeaders headers = exchange.getRequest().getHeaders();

        String token = headers.getFirst("X-API-Token");
        if (token == null || token.isBlank()) {
            LOGGER.info("Attempted to update a project build info without providing a token");
            return Mono.empty();
        }

        return Mono.just(new KeyAuthenticationToken(token));
    }

}
