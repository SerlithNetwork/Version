package net.serlith.version.service

import jakarta.transaction.Transactional
import net.serlith.version.database.repository.SoftwareRepository
import net.serlith.version.types.SubmitBuildRequest
import org.springframework.stereotype.Service

@Service
class SoftwareService (
    private val softwareRepository: SoftwareRepository,
) {

    @Transactional
    fun upsertServerBuild(data: SubmitBuildRequest) {
        this.softwareRepository.upsertBuild(data.software, data.version, data.build)
    }

}