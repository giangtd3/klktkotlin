package com.klkt.klktkotlinapi.klktutils

import java.util.*
import kotlin.jvm.internal.Intrinsics

class StringUtils {
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
    }

}