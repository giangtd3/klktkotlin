package com.klkt.klktkotlin.webapi.auth.utils

import com.klkt.klktkotlin.utils.AppErrorEnum
import com.klkt.klktkotlin.utils.MessageUtils
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCrypt
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class AuthUtils {
    companion object {
        private val log = LoggerFactory.getLogger(AuthUtils::class.java)
        const val HEADER_REMOTE_IP: String = ""
        const val SALT_SIZE: Int = 6
        fun getIpRequest(request: HttpServletRequest): String? {
            return getIpRequest(request, null)
        }

        fun getIpRequest(request: HttpServletRequest, remoteIpHeader: String?): String? {
            var ipAdd: String? = null
            try {
                if (remoteIpHeader != null) {
                    ipAdd = request.getHeader(remoteIpHeader)
                }
                if (ipAdd == null || ipAdd.isEmpty()) {
                    ipAdd = request.getHeader(HEADER_REMOTE_IP)
                }
                if (ipAdd == null || ipAdd.isEmpty()) {
                    return request.remoteAddr
                }
            } catch (e: Exception) {
                println(e.message)
                ipAdd = "unknown"
            }
            return if (ipAdd!!.indexOf(",") > 0) ipAdd.substring(0, ipAdd.indexOf(",")) else ipAdd
        }

        fun getUserAgent(request: HttpServletRequest): String? {
            return try {
                request.getHeader("User-Agent")
            } catch (e: Exception) {
                println(e.message)
                "unknown"
            }
        }

        fun isMatchPath(pattern: CharArray, url: CharArray): Boolean {
            return try {
                if (pattern.size > url.size) {
                    return false
                }
                var extBit = 0
                for (i in pattern.indices) {
                    if (i + extBit > url.size || pattern[i] != '{' && pattern[i] != url[i + extBit]) {
                        return false
                    }
                    if (pattern[i] != '{') {
                        continue
                    }
                    while (i + 1 + extBit < url.size && url[i + 1 + extBit] != '/') {
                        extBit++
                    }
                }
                pattern.size + extBit == url.size
            } catch (e: java.lang.Exception) {
                log.error("Match pattern have some error! pattern: {} - url: {}", String(pattern), String(url), e)
                true
            }
        }

        fun getPrefixPathToChar(url: String): CharArray? {
            var isSkip = false
            val bf = StringBuilder()
            for (key in url.toCharArray()) {
                if (key == '{') {
                    bf.append(key)
                    isSkip = true
                    continue
                }
                if (key == '}') {
                    isSkip = false
                    continue
                }
                if (isSkip) continue
                bf.append(key)
            }
            return bf.toString().toCharArray()
        }

        fun makeErrorResponseMsg(authError: AuthErrEnum?, errorMsg: String?): ResponseEntity<Any?> {
            return when (authError) {
                AuthErrEnum.TRUST_AUTH_TOKEN_INVALID ->
                    ResponseEntity<Any?>(
                            MessageUtils.buildWithError(AppErrorEnum.LOGIN_LOI_XAC_THUC, errorMsg).toString()
                            , HttpStatus.OK)

                AuthErrEnum.TRUST_AUTH_TOKEN_NULL_OR_EMPTY, AuthErrEnum.TRUST_AUTH_TOKEN_NOT_ACCEPT ->
                    ResponseEntity<Any?>(
                            MessageUtils.buildWithError(AppErrorEnum.LOGIN_LOI_TOKEN_KHONG_XAC_DINH, errorMsg).toString()
                            , HttpStatus.OK)

                AuthErrEnum.ACCEPT_DENIED, AuthErrEnum.DEFAULT_EXCEPTION ->
                    ResponseEntity<Any?>(
                            MessageUtils.buildWithError(AppErrorEnum.TCTTCD_LOI_DL_DAU_VAO, errorMsg).toString()
                            , HttpStatus.OK)

                else -> ResponseEntity<Any?>(
                        MessageUtils.buildWithError(AppErrorEnum.ERR_SYSTEM_ERROR, errorMsg).toString()
                        , HttpStatus.OK)
            }
        }

        fun makeErrorResponse(response: HttpServletResponse, errMsg: String?) {
            try {
                val message = makeErrorResponseMsg(AuthErrEnum.TRUST_AUTH_TOKEN_INVALID, errMsg)
                response.status = message.statusCodeValue
                response.contentType = MediaType.APPLICATION_JSON_VALUE
                response.characterEncoding = "UTF-8"
                response.writer.write(message.body.toString())
            } catch (e: java.lang.Exception) {
                log.warn("[INVALID REQUEST] response to client failed. {}", e.message)
            }
        }

        fun genPwd(passWord: String?): String? {
            return BCrypt.hashpw(passWord, BCrypt.gensalt(SALT_SIZE))
        }
    }
}