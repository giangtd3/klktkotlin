package com.klkt.klktkotlin.utils

import java.io.File
import kotlin.jvm.internal.Intrinsics

class FileUtils {
    companion object {

        fun isFile(path: String): Boolean {
            val f = File(path)
            return f.isFile
        }

        fun isExist(path: String): Boolean {
            val f = File(path)
            return f.exists()
        }

        fun mkdirs(path: String) {
            val isFile = Regex(".+[.]\\w+$").matches((path as CharSequence)!!)
            val f = File(path)
            if (isFile && !f.parentFile.exists()) {
                f.parentFile.mkdirs()
            } else if (!isFile && !f.exists()) {
                f.mkdirs()
            }
        }

        fun userDir(path: String?): String {
            if (path == null) {
                return Intrinsics.stringPlus(System.getProperty("user.dir"), "/" as Any)
            }
            return if (isExist(path) || Regex("^([.]|[/]).+").matches((path as CharSequence?)!!)) {
                path
            } else System.getProperty("user.dir") + '/' + path as Any
        }
    }
}