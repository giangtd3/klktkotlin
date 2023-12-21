package com.klkt.klktkotlin.utils

import java.io.Closeable
import java.io.PrintWriter
import java.io.StringWriter
import java.io.Writer
import java.util.*
import kotlin.jvm.internal.Intrinsics

class KLKTStringUtils {
    companion object {
        fun isNull(value: Any?): Boolean {
            return value == null
        }

        fun isEmpty(value: Any?): Boolean {
            if (!isNull(value)) {
                val string = Objects.toString(value)
                Intrinsics.checkNotNullExpressionValue(string as Any, "toString(value)")
                if ((string as CharSequence).trim().toString().isNotEmpty()) {
                    return false
                }
            }
            return true
        }


        fun writeOutException(e: Exception): String {
            return try {
                val var2 = StringWriter() as Closeable
                val var7: String
                try {
                    val sw = var2 as StringWriter
                    e.printStackTrace(PrintWriter(sw as Writer))
                    var7 = sw.toString()
                } catch (var11: Throwable) {
                    throw var11
                } finally {
                    var2.close()
                }
                var7
            } catch (var13: Exception) {
                e.toString()
            }
        }
    }

}