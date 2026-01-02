package net.serlith.version.database.types

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "server_software",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["software", "version"])
    ]
)
class ServerSoftware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    @Column(name = "software")
    lateinit var software: String

    @Column(name = "version")
    lateinit var version: String

    @Column(name = "build")
    var build: Int = 0


    // Likely unneeded
    // @OneToMany(mappedBy = "versions", cascade = [CascadeType.ALL], orphanRemoval = true)
    // var versions: MutableList<ServerVersion> = mutableListOf()

}