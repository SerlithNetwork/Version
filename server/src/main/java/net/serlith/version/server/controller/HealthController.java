package net.serlith.version.server.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/health")
public class HealthController {

    private static final Mono<String> HEALTH_OK = Mono.just("{\"status\":\"ok\"}");

    @GetMapping
    public Mono<String> getHealth() {
        return HEALTH_OK;
    }

}
