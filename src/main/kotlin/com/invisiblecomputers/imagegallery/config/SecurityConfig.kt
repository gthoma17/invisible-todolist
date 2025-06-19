package com.invisiblecomputers.imagegallery.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.header.writers.ReferrerPolicyHeaderWriter

@Configuration
@EnableWebSecurity
class SecurityConfig {
    
    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests { authz ->
                authz
                    .requestMatchers("/api/**").permitAll()
                    .requestMatchers("/settings").permitAll()
                    .requestMatchers("/h2-console/**").permitAll()
                    .anyRequest().authenticated()
            }
            .csrf { csrf ->
                csrf
                    .ignoringRequestMatchers("/api/**")
                    .ignoringRequestMatchers("/h2-console/**")
            }
            .headers { headers ->
                headers
                    .frameOptions().sameOrigin()
                    .referrerPolicy(ReferrerPolicyHeaderWriter.ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN)
            }
            .sessionManagement { session ->
                session.maximumSessions(1)
            }
        
        return http.build()
    }
}