package net.serlith.version.server.manager;

import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import net.serlith.version.server.security.authentication.KeyAuthenticationToken;
import net.serlith.version.server.service.JwtService;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@NullMarked
@RequiredArgsConstructor
public class JwtAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwts;
    private final ReactiveUserDetailsService users;

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {

        if (!(authentication instanceof KeyAuthenticationToken jwtAuthentication)) {
            return Mono.empty();
        }

        Claims claims = jwts.parseClaims(jwtAuthentication.getCredentials());
        if (claims == null) {
            return Mono.error(new BadCredentialsException("Missing Claims"));
        }

        String username = claims.getSubject();
        if (username == null) {
            return Mono.error(new BadCredentialsException("Missing User"));
        }

        return users.findByUsername(username)
                .filter(user -> jwts.areClaimsValid(claims, user))
                .map(user -> (Authentication) new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()))
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid Authorization")));
    }

}
