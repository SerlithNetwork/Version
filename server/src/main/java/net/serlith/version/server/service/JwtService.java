package net.serlith.version.server.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.security.SecureRandom;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@NullMarked
public class JwtService {

    private static final Logger LOGGER = LoggerFactory.getLogger(JwtService.class);
    private static final String ISSUER = "Yoimiya";

    private final SecureRandom random = new SecureRandom();
    private final KeyPair keypair = Jwts.SIG.RS512.keyPair()
            .random(this.random)
            .build();

    public final Duration accessLifetime = Duration.ofHours(1);
    public final Duration refreshLifetime = Duration.ofHours(12);

    public boolean areClaimsValid(Claims claims, UserDetails user) {
        String username = claims.getSubject();
        Date expiration = claims.getExpiration();
        if (username == null || expiration == null) {
            return false;
        }

        return username.equals(user.getUsername()) && !expiration.before(new Date());
    }

    @Nullable
    public Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(this.keypair.getPublic())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            LOGGER.error("Failed to authenticate JWT token");
            return null;
        }
    }

    public String createAccessToken(UserDetails user) {
        return this.createToken(user, this.accessLifetime);
    }

    public String createRefreshToken(UserDetails user) {
        return this.createToken(user, this.refreshLifetime);
    }

    private String createToken(UserDetails user, Duration lifetime) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getUsername())
                .issuer(ISSUER)
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(lifetime)))
                .signWith(this.keypair.getPrivate(), Jwts.SIG.RS512)
                .compact();
    }

}
