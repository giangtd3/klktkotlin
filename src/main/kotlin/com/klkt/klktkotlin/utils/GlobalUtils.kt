package com.klkt.klktkotlinapi.klktutils

import com.klkt.klktkotlin.utils.FileUtils
import com.klkt.klktkotlin.utils.FlagUtils
import java.io.IOException
import java.util.*
import kotlin.jvm.functions.Function1
import kotlin.jvm.internal.Intrinsics

class GlobalUtils {
    companion object {
        fun argsLoad(args: Array<String>):Companion{
            FlagUtils.load(args)
            return this
        }

        @Throws(InterruptedException::class)
        fun sleep(millisecondSleep: Long) {
            Thread.sleep(millisecondSleep)
        }

        @Throws(IOException::class)
        fun configLogger(configPath: String): Companion {
            val fullPathConfig: String = FileUtils.userDir(configPath)
            if (!FileUtils.isExist(fullPathConfig)) {
                throw IOException(Intrinsics.stringPlus("config logger does not exist. path: ", fullPathConfig as Any))
            }
            System.setProperty("log4j2.formatMsgNoLookups", "true")
            System.setProperty("logback.configurationFile", fullPathConfig)
            System.setProperty("logging.config", fullPathConfig)
            return this
        }

        fun setHttpAgent(agentName: String): Companion {
            System.setProperty("http.agent", agentName)
            return this
        }

        fun updateTimeZoneDefault(timeZone: String): Companion {
            TimeZone.setDefault(TimeZone.getTimeZone(timeZone))
            return this
        }

        fun <T : AutoCloseable?> close(
            obj: Array<T>,
            beforeClose: Function1<Any?, Any?>
        ): Companion {
            var i = 0
            while (i < obj.size) {
                val item: AutoCloseable? = obj[i]
                ++i
                try {
                    beforeClose.invoke(item as Any)
                    val autoCloseable = item
                    autoCloseable.close()
                } catch (e: Exception) {
                    println(e)
                }
            }
            return this
        }
    }
}