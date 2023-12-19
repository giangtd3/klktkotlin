package com.klkt.klktkotlin.webapi.auth.controller

import com.fasterxml.jackson.databind.JsonNode
import com.klkt.klktkotlin.webapi.auth.service.AuthenticationService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException
import utils.KLKTJsonObject

@RestController
@RequestMapping("/api/auth")
class AuthController(private val authenticationService: AuthenticationService) {
    /*
        request: {username: "", password: ""}
        response: {accessToken:"", refreshToken:""}
     */
    @PostMapping
    fun authenticate(
        @RequestBody authRequest: JsonNode
    ): JsonNode = authenticationService.authentication(authRequest).toJsonNode()

    /*
         request: {token: "refreshToken"}
         response: {token:"new"}
    */
    @PostMapping("/refresh")
    fun refreshAccessToken(
        @RequestBody request: JsonNode
    ): JsonNode =
        authenticationService.refreshAccessToken(request.get("token").asText(""))
            ?.mapToTokenResponse()!!.toJsonNode()
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid refresh token.")

    private fun String.mapToTokenResponse(): KLKTJsonObject = KLKTJsonObject.newObj().put("token", this)
}