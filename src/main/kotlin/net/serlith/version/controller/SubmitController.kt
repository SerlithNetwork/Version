package net.serlith.version.controller

import jakarta.servlet.http.HttpServletRequest
import net.serlith.version.service.SoftwareService
import net.serlith.version.types.SubmitBuildRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/submit")
class SubmitController

@Autowired
constructor(
    private val softwareService: SoftwareService,
) {

    private final val logger = LoggerFactory.getLogger(SubmitController::class.java)

    @PostMapping
    fun submitCommit(
        request: HttpServletRequest,
        @RequestBody data: SubmitBuildRequest,
    ): ResponseEntity<Boolean> {

        val softwareHeader = request.getHeader("Server-Software")
        if (!data.software.equals(softwareHeader, ignoreCase = true)) {
            this.logger.info("Provided different software compared to the header")
            return ResponseEntity.badRequest().body(false)
        }
        try {
            this.softwareService.upsertServerBuild(data)
        } catch (e: Exception) {
            this.logger.info("Failed to perform database commit", e)
            return ResponseEntity.badRequest().body(false)
        }

        this.logger.info("Successfully updated software build")
        return ResponseEntity.ok(true)
    }

}