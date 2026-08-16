package net.serlith.version.server.controller;

import io.jsonwebtoken.Claims;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import net.serlith.version.server.manager.PasswordAuthenticationManager;
import net.serlith.version.server.service.JwtService;
import net.serlith.version.server.types.authentication.AuthenticationDetails;
import net.serlith.version.server.types.authentication.PasswordAuthenticationForm;
import net.serlith.version.server.types.authentication.TokenAuthenticationForm;
import net.serlith.version.server.types.management.PrivilegedUserDetails;
import net.serlith.version.server.types.token.ExpirableToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/authentication")
public class AuthenticationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationController.class);

    private final PasswordAuthenticationManager authenticationManager;
    private final ReactiveUserDetailsService users;
    private final JwtService jwts;

    @PostMapping("/password")
    public Mono<AuthenticationDetails> authenticatePassword(
            ServerHttpRequest http,

            @Valid
            @RequestBody
            PasswordAuthenticationForm request
    ) {

        LOGGER.info("Attempting to authorize user [{}]", request.username());
        return this.authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.username(), request.password()))
                .flatMap(authentication -> {
                    // Create authentication details
                    return this.users.findByUsername(authentication.getName())
                            .map(user -> {
                                String accessToken = this.jwts.createAccessToken(user);
                                String refreshToken = this.jwts.createRefreshToken(user);
                                return new AuthenticationDetails(
                                        new PrivilegedUserDetails(user.getUsername()),
                                        new ExpirableToken(accessToken, this.jwts.accessLifetime.getSeconds()),
                                        new ExpirableToken(refreshToken, this.jwts.refreshLifetime.getSeconds())
                                );
                            });
                }).onErrorResume(ignore -> {
                    LOGGER.info("Failed to authorize user [{}] at [{}]", request.username(), http.getRemoteAddress());
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credentials not valid"));
                });
    }

    @PostMapping("/token")
    public Mono<AuthenticationDetails> authenticateToken(
            ServerHttpRequest http,

            @Valid
            @RequestBody
            TokenAuthenticationForm request
    ) {

        LOGGER.info("Attempting to authorize with refresh token");
        Claims claims = this.jwts.parseClaims(request.token());
        if (claims == null) {
            LOGGER.info("Failed to parse token claims at [{}]", http.getRemoteAddress());
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not valid"));
        }

        String username = claims.getSubject();
        if (username == null) {
            LOGGER.info("Failed to get subject at [{}]", http.getRemoteAddress());
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not valid"));
        }

        return this.users.findByUsername(username)
                .flatMap(user -> {
                    if (!this.jwts.areClaimsValid(claims, user)) {
                        LOGGER.info("Failed authenticate claims for [{}] at [{}]", username, http.getRemoteAddress());
                        return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token not valid"));
                    }

                    String accessToken = this.jwts.createAccessToken(user);
                    String refreshToken = this.jwts.createRefreshToken(user);
                    return Mono.just(
                            new AuthenticationDetails(
                                    new PrivilegedUserDetails(user.getUsername()),
                                    new ExpirableToken(accessToken, this.jwts.accessLifetime.getSeconds()),
                                    new ExpirableToken(refreshToken, this.jwts.refreshLifetime.getSeconds())
                            )
                    );
                });
    }

}
