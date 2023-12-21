package com.klkt.klktkotlin.webapi.auth.service

import com.klkt.klktkotlin.webapi.auth.config.JwtProperties
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.jvm.Throws

@Service
class TokenService(jwtProperties: JwtProperties) {
    private val secretKey = Keys.hmacShaKeyFor(
            jwtProperties.key.toByteArray()
    )

    fun generate(
            userDetails: UserDetails,
            expirationDate: Date,
            additionalClaims: Map<String, Any> = emptyMap()
    ): String =
            Jwts.builder()
                    .claims()
                    .subject(userDetails.username)
                    .issuedAt(Date(System.currentTimeMillis()))
                    .expiration(expirationDate)
                    .add(additionalClaims)
                    .and()
                    .signWith(secretKey)
                    .compact()

    fun isValid(token: String, userDetails: UserDetails): Boolean {
        val email = extractUsername(token)

        return userDetails.username == email && !isExpired(token)
    }

    @Throws
    fun extractUsername(token: String): String? {
        val claims: Claims? = getAllClaims(token)
        if (claims != null) {
            return claims.subject
        }
        return null
    }


    fun isExpired(token: String): Boolean {
        val claims: Claims? = getAllClaims(token)
        if (claims != null) {
            return claims.expiration.before(Date(System.currentTimeMillis()))
        }
        return false
    }


    @Throws
    private fun getAllClaims(token: String): Claims? {

        val parser = Jwts.parser()
                .verifyWith(secretKey)
                .build()
        return parser
                .parseSignedClaims(token)
                .payload

    }


}