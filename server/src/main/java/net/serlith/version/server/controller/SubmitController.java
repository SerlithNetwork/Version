package net.serlith.version.server.controller;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.service.ServerSoftwareService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/submit")
public class SubmitController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SubmitController.class);

    private final ServerSoftwareService service;

    @PostMapping
    public Mono<Boolean> submitProject() {
    }

}
