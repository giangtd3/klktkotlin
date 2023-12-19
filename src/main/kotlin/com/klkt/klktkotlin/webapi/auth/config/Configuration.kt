package com.klkt.klktkotlin.webapi.auth.config

import com.klkt.klktkotlin.webapi.auth.repo.KLKTUserRepo
import com.klkt.klktkotlin.webapi.auth.service.CustomUserDetailsService
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder

@Configuration
@EnableConfigurationProperties(JwtProperties::class)
@ComponentScan(value = arrayOf("com.klkt.klktkotlin.webapi.auth"))
class Configuration {
    @Bean
    fun userDetailsService(userRepo: KLKTUserRepo): UserDetailsService =
        CustomUserDetailsService(userRepo)

    @Bean
    fun authenticationProvider(userRepo: KLKTUserRepo): AuthenticationProvider =
        DaoAuthenticationProvider()
            .also {
                it.setUserDetailsService(userDetailsService(userRepo))
                it.setPasswordEncoder(encoder())
            }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager =
        config.authenticationManager
    @Bean
    fun encoder(): PasswordEncoder = BCryptPasswordEncoder()
}