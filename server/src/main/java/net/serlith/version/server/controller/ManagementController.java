package net.serlith.version.server.controller;

import lombok.RequiredArgsConstructor;
import net.serlith.version.server.service.ServerSoftwareService;
import net.serlith.version.server.types.SoftwareDataTokenized;
import net.serlith.version.server.types.SoftwareDataTokenless;
import net.serlith.version.server.types.management.PrivilegedUserDetails;
import net.serlith.version.server.types.software.SoftwareUpdateRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/management")
public class ManagementController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementController.class);

    private final ServerSoftwareService softwareService;

    @GetMapping("/privileged/self")
    public Mono<PrivilegedUserDetails> getUserDetails(
            @AuthenticationPrincipal
            UserDetails user
    ) {
        return Mono.just(new PrivilegedUserDetails(user.getUsername()));
    }

    @GetMapping("/software")
    public Flux<SoftwareDataTokenless> fetchAllSoftware(
            @AuthenticationPrincipal
            UserDetails user
    ) {
        LOGGER.info("User [{}] is fetching all software...", user.getUsername());
        return this.softwareService.fetchAllServers();
    }

    @PostMapping("/software")
    public Mono<SoftwareDataTokenized> createSoftware(
            @RequestBody
            SoftwareUpdateRequest request,

            @AuthenticationPrincipal
            UserDetails user
    ) {
        LOGGER.info("User [{}] is registering a software [{}]...", user.getUsername(), request.name());
        return this.softwareService.createServer(request);
    }

    @PutMapping("/software/{id}")
    public Mono<SoftwareDataTokenless> updateSoftware(
            @PathVariable
            long id,

            @RequestBody
            SoftwareUpdateRequest request,

            @AuthenticationPrincipal
            UserDetails user
    ) {
        LOGGER.info("User [{}] is updating a software with id [{}] to use name [{}] and display [{}]...", user.getUsername(), id, request.name(), request.displayName());
        return this.softwareService.updateSoftware(id, request);
    }

    @PostMapping("/software/{id}/reset")
    public Mono<SoftwareDataTokenized> resetSoftwareToken(
            @PathVariable
            long id,

            @AuthenticationPrincipal
            UserDetails user
    ) {
        LOGGER.info("User [{}] is resetting the token for a software with id [{}]...", user.getUsername(), id);
        return this.softwareService.resetSoftwareToken(id);
    }

    @DeleteMapping("/software/{id}")
    public Mono<Integer> deleteSoftware(
            @PathVariable
            long id,

            @AuthenticationPrincipal
            UserDetails user
    ) {
        LOGGER.info("User [{}] is deleting a software with id [{}]...", user.getUsername(), id);
        return this.softwareService.deleteSoftware(id);
    }

}
