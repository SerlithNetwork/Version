package net.serlith.version.server.configuration;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.manager.*;
import org.jspecify.annotations.NullMarked;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import java.util.List;

@NullMarked
@Configuration
@RequiredArgsConstructor
public class SecurityConfiguration {

    @Value("${version.cors.allowed-origins}")
    private List<String> allowedOrigins;

    @Bean
    @Primary
    public ReactiveAuthenticationManager fallbackAuthenticationManager() {
        // Hello, I'm John Spring and I like primary authentication managers
        return (ignore) -> Mono.error(
                new UnsupportedOperationException("Unsupported")
        );
    }

    @Bean
    public PasswordAuthenticationManager passwordAuthenticationManager(ReactiveUserDetailsService users, PasswordEncoder encoder) {
        PasswordAuthenticationManager manager = new PasswordAuthenticationManager(users);
        manager.setPasswordEncoder(encoder);
        return manager;
    }

    @Bean
    @Order(1)
    public SecurityWebFilterChain jwtChainManagement(ServerHttpSecurity http, JwtAuthenticationManager manager, JwtAuthenticationConverter converter) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(manager);
        filter.setServerAuthenticationConverter(converter);
        return http.securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/v1/management/**"))
                .authorizeExchange(ex -> ex.anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(spec -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(this.allowedOrigins);
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));

                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", config);

                    spec.configurationSource(source);
                })
                .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    @Order(2)
    public SecurityWebFilterChain ciUpdateChain(ServerHttpSecurity http, CiAuthenticationManager manager, CiAuthenticationConverter converter) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(manager);
        filter.setServerAuthenticationConverter(converter);
        return http.securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/v1/project/submit"))
                .authorizeExchange(ex -> ex.anyExchange().authenticated())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .addFilterAt(filter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    @Order(20)
    public SecurityWebFilterChain authenticationChain(ServerHttpSecurity http) {
        return http.securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/v1/authentication/**"))
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(spec -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(this.allowedOrigins);
                    config.setAllowedMethods(List.of("GET", "POST", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));

                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", config);

                    spec.configurationSource(source);
                })
                .build();
    }

    @Bean
    @Order(20)
    public SecurityWebFilterChain openChain(ServerHttpSecurity http) {
        return http.securityMatcher(new PathPatternParserServerWebExchangeMatcher("/api/v1/project/fetch/**"))
                .authorizeExchange(ex -> ex.anyExchange().permitAll())
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(spec -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(List.of("*"));
                    config.setAllowedMethods(List.of("GET", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));

                    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
                    source.registerCorsConfiguration("/**", config);

                    spec.configurationSource(source);
                })
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
    }

}
