package net.serlith.version.server.controller;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.service.ServerSoftwareService;
import net.serlith.version.server.types.SubmitBuildRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    private final ServerSoftwareService service;

    @GetMapping("/fetch/{software}/{version}/latest")
    public Mono<?> fetchLatest(
            @PathVariable
            String software,

            @PathVariable
            String version
    ) {
    }

    @PostMapping("/submit")
    public Mono<Boolean> submitProject(
            @RequestBody
            SubmitBuildRequest request
    ) {
        return this.service.mergeServerBuild(request)
                .map(ignore -> true);
    }

}
