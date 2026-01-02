package net.serlith.version.controller

import net.serlith.version.database.repository.SoftwareRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/projects")
class FetchController (
    private val softwareRepository: SoftwareRepository,
) {

    @GetMapping("/{software}/{version}/latest")
    fun fetchLatest(
        @PathVariable("software") software: String,
        @PathVariable("version") version: String,
    ): ResponseEntity<Int> {
        try {
            val data = this.softwareRepository.findBySoftwareAndVersion(software = software, version = version) ?: return ResponseEntity.notFound().build()
            return ResponseEntity.ok(data.build)
        } catch (_: Exception) {}
        return ResponseEntity.badRequest().build()
    }

}