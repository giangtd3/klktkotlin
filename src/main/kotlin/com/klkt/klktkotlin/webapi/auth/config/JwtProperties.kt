package com.klkt.klktkotlin.webapi.auth.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("jwt")
data class JwtProperties(
    var key: String ="",
    var accessTokenExpiration: Long = 0L,
    var refreshTokenExpiration: Long = 0L
)
