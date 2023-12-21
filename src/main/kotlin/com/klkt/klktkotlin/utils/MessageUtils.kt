package com.klkt.klktkotlin.utils

import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory
import org.springframework.util.Base64Utils
import org.xml.sax.InputSource
import KLKTJavaUtils.JsUtils
import KLKTJavaUtils.KLKTJsonObject
import java.io.ByteArrayOutputStream
import java.io.StringReader
import java.security.MessageDigest
import java.security.SecureRandom
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathFactory

class MessageUtils {
    companion object {
        private val log = LoggerFactory.getLogger(MessageUtils::class.java)
        const val ERROR_CODE = "errorCode"
        const val ERROR_MESSAGE = "message"

        const val ERR_CODE_TAG = "Err_code"
        const val ERR_MESSAGE_TAG = "Err_msg"

        fun rebuildMsgResponse(jsData: KLKTJsonObject) {
            var row: MutableMap.MutableEntry<String, JsonNode>
            var rowValue: KLKTJsonObject
            for (node in jsData.toJsonNode()) {
                val item = node.fields()
                while (item.hasNext()) {
                    row = item.next()
                    rowValue = KLKTJsonObject.build(JsUtils.toString(row.value))
                    if (rowValue.isJson()) {
                        row.setValue(rowValue.toJsonNode())
                    }
                }
            }
        }

        fun splitError(text: String?, beginTag: String?, endTag: String?, defVal: String?): String? {
            if (null == text || null == beginTag || null == endTag) {
                return defVal
            }
            val idxSt = if (beginTag.isEmpty()) 0 else text.indexOf(beginTag)
            val idxEnd = if (endTag.isEmpty()) text.length else text.indexOf(endTag)
            return if (idxSt < 0 || idxEnd < 0 || idxSt >= idxEnd) {
                defVal
            } else text.substring(idxSt + beginTag.length, idxEnd)
        }


        fun buildWithError(error: AppErrorEnum, detailMsg: String?): KLKTJsonObject? {
            return buildWithError(error.code, error.value + (if (detailMsg != null) "! ; Detail message: $detailMsg" else ""), null)
        }

        fun buildWithError(error: AppErrorEnum, data: KLKTJsonObject?): KLKTJsonObject? {
            return buildWithError(error.code, error.value, data)
        }

        fun buildWithError(error: Int, errorMsg: String?, data: KLKTJsonObject?): KLKTJsonObject? {
            return buildWithTag(ERROR_CODE, ERROR_MESSAGE, error, errorMsg, data)
        }

        fun buildWithTagErr(error: AppErrorEnum): KLKTJsonObject? {
            return buildWithTagErr(error.code, error.value, null)
        }

        fun buildWithTagErr(error: AppErrorEnum, data: KLKTJsonObject?): KLKTJsonObject? {
            return buildWithTagErr(error.code, error.value, data)
        }

        fun buildWithTagErr(error: Int, errorMsg: String?, data: KLKTJsonObject?): KLKTJsonObject? {
            return buildWithTag(ERR_CODE_TAG, ERR_MESSAGE_TAG, String.format("%02d", error), errorMsg, data)
        }

        fun buildWithTag(errorTag: String?, msgTag: String?, error: Any?, errorMsg: Any?, data: KLKTJsonObject?): KLKTJsonObject? {
            val msgResponse: KLKTJsonObject = KLKTJsonObject.newObj()
            do {
                msgResponse.put(errorTag, error)
                msgResponse.put(msgTag, errorMsg)
                if (data == null) {
                    break
                }
                if (data.isArray()) {
                    log.error("buildWithErrTag failed! Not accept data is arrays: ", data)
                    break
                }
                msgResponse.append(data)
            } while (false)
            return msgResponse
        }

        fun buildWithException(ex: Exception): KLKTJsonObject? {
            val msgResponse: KLKTJsonObject = KLKTJsonObject.newObj()
            var msgEx = ex.message
            var error: Int = AppErrorEnum.ERR_SYSTEM_ERROR.code
            var errorMsg: String = AppErrorEnum.ERR_SYSTEM_ERROR.value
            do {
                //phan hoi msg loi. neu khong phai tu loi database thi bo qua
                if (!msgEx!!.startsWith("ORA") || !msgEx.contains(": $")) {
                    break
                }
                msgEx = splitError(msgEx, ": $", "\n", "")!!.trim()

                //Neu khong phai loi custom co dinh dang: <ma loi> - <mo ta loi>. thi bo qua
                if (!msgEx.matches("^[0-9]{1,10}\\s*[-].*".toRegex())) {
                    errorMsg = "$errorMsg $msgEx"
                    break
                }

                //map lai thong tin ma loi + mo ta loi va gui lai cho client
//                error = StringUtils.toInt(splitError(msgEx, "", "-", "").trim(), error)
                errorMsg = splitError(msgEx, "-", "", errorMsg)!!.trim()
            } while (false)
            msgResponse.put("errorCode", error.toString())
            msgResponse.put("message", errorMsg)
            return msgResponse
        }

        //XML Webservice utils:
        @Throws(Exception::class)
        open fun mapXmlToJson(jsonData: KLKTJsonObject, strXML: String?) {
            val dbf = DocumentBuilderFactory.newInstance()
            val db = dbf.newDocumentBuilder()
            val xml = db.parse(InputSource(StringReader(strXML)))
            val xpf = XPathFactory.newInstance()
            val xpath = xpf.newXPath()
            var xmlValue = ""
            for ((key, value) in jsonData.fields().entries) {
                if (value != null && !KLKTStringUtils.isEmpty(value.toString(""))) {
                    xmlValue = xpath.evaluate(value.toString(""), xml,
                            XPathConstants.STRING) as String
                    jsonData.put(key, xmlValue)
                }
            }
        }

        fun CreatePasswordDigrest(_username: String, _password: String): Map<String, String>? {
            val mapInfo: MutableMap<String, String> = HashMap()
            // From the spec: Password_Digest = Base64 ( SHA-1 ( nonce + created + password
            // ) )
            try {
                val rand = SecureRandom.getInstance("SHA1PRNG")
                rand.setSeed(System.currentTimeMillis())
                val nonceBytes = ByteArray(16)
                rand.nextBytes(nonceBytes)

                // Make the created date
                val df: DateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                df.timeZone = TimeZone.getTimeZone("UTC")
                val createdDate = df.format(Calendar.getInstance().time)
                val createdDateBytes = createdDate.toByteArray(charset("UTF-8"))

                // Make the password
                val passwordBytes = _password.toByteArray(charset("UTF-8"))

                // SHA-1 hash the bunch of it.
                val baos = ByteArrayOutputStream()
                baos.write(nonceBytes)
                baos.write(createdDateBytes)
                baos.write(passwordBytes)
                val md = MessageDigest.getInstance("SHA-1")
                val digestedPassword = md.digest(baos.toByteArray())

                // Encode the password and nonce for sending
                val passwordB64 = Base64Utils.encodeToString(digestedPassword)
                val nonceB64 = Base64Utils.encodeToString(nonceBytes)

                // ----------return------------
                mapInfo["strUsername"] = _username
                mapInfo["strPassword"] = passwordB64
                mapInfo["strNonce"] = nonceB64
                mapInfo["strCreated"] = createdDate
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            return mapInfo
        }
    }
}