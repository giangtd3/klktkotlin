package com.klkt.klktkotlin.webapi.auth.config

import com.klkt.klktkotlin.webapi.auth.service.CustomUserDetailsService
import com.klkt.klktkotlin.webapi.auth.service.TokenService
import com.klkt.klktkotlin.webapi.auth.utils.AuthUtils.Companion.makeErrorResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
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
                return
            }
        } catch (e: SignatureException) {
            log.error("Invalid JWT signature: {}", e)
            errMsg = "Invalid JWT signature: $e"
        } catch (e: MalformedJwtException) {
            log.error("Invalid JWT token: {}", e)
            errMsg = "Invalid JWT token: $e"
        } catch (e: ExpiredJwtException) {
            log.error("JWT token is expired: {}", e)
            errMsg = "JWT token is expired: $e"
        } catch (e: UnsupportedJwtException) {
            log.error("JWT token is unsupported: {}", e)
            errMsg = "JWT token is unsupported: $e"
        } catch (e: IllegalArgumentException) {
            log.error("JWT claims string is empty: {}", e)
            errMsg = "JWT claims string is empty: $e"
        } catch (e: Exception) {
            log.error("Unknow error: {}", e)
            errMsg = "Unknow error: $e"
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


    companion object {
        private val log = LoggerFactory.getLogger(this::class.java)
    }
}