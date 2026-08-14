package net.serlith.version.server.configuration;

import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import reactor.core.publisher.Mono;

import java.util.List;

@NullMarked
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Value("${yoimiya.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    @Primary
    public ReactiveAuthenticationManager fallbackAuthenticationManager() {
        // Hello, I'm John Spring and I like primary authentication managers
        return (ignore) -> Mono.error(
                new UnsupportedOperationException("Unsupported")
        );
    }

}
