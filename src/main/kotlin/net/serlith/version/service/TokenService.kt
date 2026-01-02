package net.serlith.version.service

import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import tools.jackson.databind.ObjectMapper
import tools.jackson.dataformat.yaml.YAMLFactory
import tools.jackson.module.kotlin.kotlinModule
import tools.jackson.module.kotlin.readValue
import java.io.File
import java.util.concurrent.TimeUnit

@Service
class TokenService {

    private final val config = File("config")
    private final val tokensFile = File(this.config, "tokens.yml")
    private final val objectMapper = ObjectMapper(YAMLFactory())

    private final var tokens: Set<Token> = emptySet()

    data class Token(val key: String, val software: String)
    data class Tokens(val tokens: List<Token> = listOf(Token("default_token", "paper")))

    @PostConstruct
    fun init() {
        this.config.mkdirs()
        if (!this.tokensFile.exists()) {
            this.createDefaultConfig()
        }
        this.loadConfig()
    }

    private final fun createDefaultConfig() {
        val default = Tokens()
        this.objectMapper.writeValue(this.tokensFile, default)
    }

    private final fun loadConfig() {
        val input: Tokens = this.objectMapper.readValue(this.tokensFile)
        this.tokens = input.tokens.toSet()
    }

    final fun isValid(token: String?, software: String?): Boolean {
        if (token == null || software == null) {
            return false
        }
        return this.tokens.any { it.key == token && it.software.equals(software, ignoreCase = true) }
    }

    @Scheduled(fixedRate = 10, timeUnit = TimeUnit.MINUTES)
    private fun performUpdateConfig() {
        this.loadConfig()
    }

}