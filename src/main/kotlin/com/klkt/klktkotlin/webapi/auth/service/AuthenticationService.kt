package com.klkt.klktkotlin.webapi.auth.service

import com.fasterxml.jackson.databind.JsonNode
import com.klkt.klktkotlin.webapi.auth.config.JwtProperties
import com.klkt.klktkotlin.webapi.auth.repo.RefreshTokenRepo
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import utils.KLKTJsonObject
import java.util.*

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JwtProperties,
    private val refreshTokenRepo: RefreshTokenRepo,
) {
    fun authentication(authenticationRequest: JsonNode): KLKTJsonObject {
        authManager.authenticate(
            UsernamePasswordAuthenticationToken(
                authenticationRequest.get("username").asText(""),
                authenticationRequest.get("password").asText("")
            )
        )

        val user = userDetailsService.loadUserByUsername(authenticationRequest.get("username").asText(""))

        val accessToken = createAccessToken(user)
        val refreshToken = createRefreshToken(user)

        refreshTokenRepo.save(refreshToken, user)

        return KLKTJsonObject.newObj().put("accessToken", accessToken).put("refreshToken", refreshToken)

//        return AuthenticationResponse(
//            accessToken = accessToken,
//            refreshToken = refreshToken
//        )
    }

    fun refreshAccessToken(refreshToken: String): String? {
        val extractedUsername = tokenService.extractUsername(refreshToken)

        return extractedUsername?.let { username ->
            val currentUserDetails = userDetailsService.loadUserByUsername(username)
            val refreshTokenUserDetails = refreshTokenRepo.findUserDetailsByToken(refreshToken)

            if (!tokenService.isExpired(refreshToken) && refreshTokenUserDetails?.username == currentUserDetails.username)
                createAccessToken(currentUserDetails)
            else
                null
        }
    }

    private fun createAccessToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = getAccessTokenExpiration()
    )

    private fun createRefreshToken(user: UserDetails) = tokenService.generate(
        userDetails = user,
        expirationDate = getRefreshTokenExpiration()
    )

    private fun getAccessTokenExpiration(): Date =
        Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration)

    private fun getRefreshTokenExpiration(): Date =
        Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration)
}