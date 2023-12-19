package com.klkt.klktkotlin.webapi.auth.config

import com.klkt.klktkotlin.webapi.auth.service.CustomUserDetailsService
import com.klkt.klktkotlin.webapi.auth.service.TokenService
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.lang.IllegalArgumentException
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter(
        private val userDetailsService: CustomUserDetailsService,
        private val tokenService: TokenService
) : OncePerRequestFilter() {
    override fun doFilterInternal(
            request: HttpServletRequest,
            response: HttpServletResponse,
            filterChain: FilterChain
    ) {
        var errMsg = ""
        try {
            val authHeader: String? = request.getHeader("Authorization")

            if (authHeader.doesNotContainBearerToken()) {
                filterChain.doFilter(request, response)
                return
            }

            val jwtToken = authHeader!!.extractTokenValue()
            val username = tokenService.extractUsername(jwtToken)

            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val foundUser = userDetailsService.loadUserByUsername(username)

                if (tokenService.isValid(jwtToken, foundUser))
                    updateContext(foundUser, request)

                filterChain.doFilter(request, response)
            }
        } catch (e: SignatureException) {
            log.error("Invalid JWT signature: {}", e.message)
            errMsg = String.format("Invalid JWT signature: {}", e.message)
        } catch (e: MalformedJwtException) {
            log.error("Invalid JWT token: {}", e.message)
            errMsg = String.format("Invalid JWT token: {}", e.message)
        } catch (e: ExpiredJwtException) {
            log.error("JWT token is expired: {}", e.message)
            errMsg = String.format("JWT token is expired: {}", e.message)
        } catch (e: UnsupportedJwtException) {
            log.error("JWT token is unsupported: {}", e.message)
            errMsg = String.format("JWT token is unsupported: {}", e.message)
        } catch (e: IllegalArgumentException) {
            log.error("JWT claims string is empty: {}", e.message)
            errMsg = String.format("JWT claims string is empty: {}", e.message)
        } catch (e: Exception) {
            log.error("Unknow error: {}", e.message)
            errMsg = String.format("Unknow error: {}", e.message)
        }

        makeErrorResponse(response, errMsg)
    }


    private fun String?.doesNotContainBearerToken() =
            this == null || !this.startsWith("Bearer ")

    private fun String.extractTokenValue() =
            this.substringAfter("Bearer ")

    private fun updateContext(foundUser: UserDetails, request: HttpServletRequest) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
        SecurityContextHolder.getContext().authentication = authToken
    }

    fun makeErrorResponse(response: HttpServletResponse, authError: String) {
        try {
            val message: ResponseEntity<String?> = makeErrorResponseMsg(authError)
            response.status = message.statusCodeValue
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            response.characterEncoding = "UTF-8"
            response.writer.write(message.body)
        } catch (e: Exception) {
            logger.warn("[INVALID REQUEST] response to client failed. {}", e)
        }
    }

    fun makeErrorResponseMsg(authError: String): ResponseEntity<String?> {
        return when (authError) {
            "TRUST_AUTH_TOKEN_INVALID" -> //                return new ResponseEntity<>("Error: Unauthorized"
                //                        , HttpStatus.UNAUTHORIZED);
                ResponseEntity<String?>(
                        "TRUST_AUTH_TOKEN_INVALID", HttpStatus.OK)

            "TRUST_AUTH_TOKEN_NULL_OR_EMPTY", "TRUST_AUTH_TOKEN_NOT_ACCEPT" -> //
                ResponseEntity<String?>("TRUST_AUTH_TOKEN_NULL_OR_EMPTY, TRUST_AUTH_TOKEN_NOT_ACCEPT", HttpStatus.OK)

            "ACCEPT_DENIED", "DEFAULT_EXCEPTION" ->
                ResponseEntity<String?>("ACCEPT_DENIED, DEFAULT_EXCEPTION", HttpStatus.OK)

            else -> ResponseEntity<String?>(authError, HttpStatus.OK)
        }
    }
    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}