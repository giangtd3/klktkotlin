package com.klkt.klktkotlin.webapi.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
@ComponentScan(value = arrayOf("com.klkt.klktkotlin.webapi.auth"))
class SecurityConfiguration( private val authenticationProvider: AuthenticationProvider) {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JwtAuthenticationFilter
    ): DefaultSecurityFilterChain {
        http.csrf { it.disable() }
            .authorizeHttpRequests {it
                    .antMatchers("/api/auth", "api/auth/refresh", "/error")
                    .permitAll()
                    .antMatchers(HttpMethod.POST, "/api/user")
                    .permitAll()
                    .antMatchers("/api/user**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)

        return http.build()
    }
}