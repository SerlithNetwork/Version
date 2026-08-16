package net.serlith.version.server.controller;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.security.authentication.KeyAuthenticationToken;
import net.serlith.version.server.service.ServerSoftwareService;
import net.serlith.version.server.types.ServerVersionData;
import net.serlith.version.server.types.SubmitBuildRequest;
import net.serlith.version.server.types.VersionData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/project")
public class ProjectController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectController.class);

    private final ServerSoftwareService service;

    @GetMapping("/fetch/{software}/{version}/latest")
    public Mono<VersionData> fetchLatest(
            @PathVariable
            String software,

            @PathVariable
            String version
    ) {
        return this.service.fetchServerData(software, version);
    }

    @GetMapping("/fetch/{software}/{version}/latest/build")
    public Mono<Long> fetchLatestBuild(
            @PathVariable
            String software,

            @PathVariable
            String version
    ) {
        return this.service.fetchServerData(software, version)
                .map(VersionData::build);
    }

    @PostMapping("/submit")
    public Mono<ServerVersionData> submitProject(
            @RequestBody
            SubmitBuildRequest request,

            KeyAuthenticationToken authentication
    ) {
        if (!authentication.allowsSoftware(request.software())) {
            LOGGER.info("CI for [{}] attempted to update server [{}] build data without authorization", authentication.getPrincipal(), request.software());
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED));
        }
        LOGGER.info("CI for [{}] is updating server [{}] build data...", authentication.getPrincipal(), request.software());
        return this.service.mergeServerBuild(request)
                .onErrorResume(throwable -> {
                    LOGGER.error("CI for [{}] failed to update server [{}] build data...", authentication.getPrincipal(), request.software());
                    return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, throwable.getMessage()));
                });
    }

}
