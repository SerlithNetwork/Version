package net.serlith.version.server.util;

import lombok.experimental.UtilityClass;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.security.SecureRandom;

@UtilityClass
public class TokenUtils {

    private final String CHARS_LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private final String CHARS_UPPERCASE = CHARS_LOWERCASE.toUpperCase();
    private final String NUMBERS = "0123456789";
    private final String DATA_FOR_RANDOM_STRING = CHARS_LOWERCASE + CHARS_UPPERCASE + NUMBERS;
    private final SecureRandom random = new SecureRandom();

    public Mono<Tuple2<Long, String>> parseToken(String token) {
        String[] splits = token.split("\\.");
        if (splits.length != 2) {
            return Mono.empty();
        }

        final long id;
        try {
            id = Long.parseLong(splits[0], 16);
        } catch (NumberFormatException e) {
            return Mono.empty();
        }

        return Mono.zip(
                Mono.just(id),
                Mono.just(splits[1])
        );
    }

    public static String generateRandomKey(int length) {
        if (length < 8) {
            throw new IllegalArgumentException("Requires at least length 1");
        }
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int rndCharAt = random.nextInt(DATA_FOR_RANDOM_STRING.length());
            char rndChar = DATA_FOR_RANDOM_STRING.charAt(rndCharAt);
            sb.append(rndChar);
        }
        return sb.toString();
    }

}
