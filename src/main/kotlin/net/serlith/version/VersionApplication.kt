package net.serlith.version

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class VersionApplication

fun main(args: Array<String>) {
    runApplication<VersionApplication>(*args)
}
