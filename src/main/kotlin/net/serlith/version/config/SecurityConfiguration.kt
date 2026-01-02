package net.serlith.version.config

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import net.serlith.version.service.TokenService
import org.apache.logging.log4j.util.Strings
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import org.springframework.web.filter.CorsFilter
import org.springframework.web.filter.OncePerRequestFilter

@Configuration
class SecurityConfiguration

@Autowired
constructor(
    private val tokenService: TokenService,
) {

    private final val logger = LoggerFactory.getLogger(SecurityConfiguration::class.java)

    @Bean
    fun corsFilter(): CorsFilter {
        val config = CorsConfiguration().apply {
            this.allowedOrigins = listOf("*")
            this.allowedMethods = listOf("GET", "POST")
            this.allowedHeaders = listOf("*")
            this.allowCredentials = false
        }
        val source = UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", config)
        }
        return CorsFilter(source)
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests {
            it.requestMatchers(HttpMethod.GET, "/**").permitAll()
            it.requestMatchers(HttpMethod.POST, "/**").permitAll()
        }.csrf {
            it.disable()
        }.addFilterBefore(this.authFilter(), UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }

    private fun authFilter(): OncePerRequestFilter = object : OncePerRequestFilter() {
        override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain,
        ) {
            if (request.method == "POST") {
                val token = request.getHeader("Authorization")?.trim()
                val software = request.getHeader("Server-Software")?.trim()
                if (!this@SecurityConfiguration.tokenService.isValid(token, software)) {
                    this@SecurityConfiguration.logger.warn("Attempted unauthorized access from ${request.remoteAddr}:${request.remotePort} using token $token")
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token")
                    return
                }
            }
            if (request.method == "GET") {
                val agent = request.getHeader("User-Agent")
                if (Strings.isBlank(agent)) {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid agent")
                    return
                }
                this@SecurityConfiguration.logger.info("Request from ${request.remoteAddr}:${request.remotePort} using agent [$agent]")
            }
            filterChain.doFilter(request, response)
        }
    }

}