package net.serlith.version.server.types.token;

public record ExpirableToken(String token, long expiration) {
}
