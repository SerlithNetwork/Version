package net.serlith.version.database.repository

import net.serlith.version.database.types.ServerSoftware
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface SoftwareRepository : CrudRepository<ServerSoftware, Long> {

    @Modifying
    @Query("""
        MERGE INTO server_software (software, version, build)
        KEY (software, version)
        VALUES (:software, :version, :build)
    """,
        nativeQuery = true
    )
    fun upsertBuild(software: String, version: String, build: Int)
    fun findBySoftwareAndVersion(software: String, version: String): ServerSoftware?

}