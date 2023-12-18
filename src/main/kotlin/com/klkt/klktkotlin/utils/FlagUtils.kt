package com.klkt.klktkotlin.utils

import java.io.IOException
import java.util.concurrent.ConcurrentHashMap
import kotlin.jvm.internal.Intrinsics
import kotlin.text.*

class FlagUtils {

    companion object {
        private var mapFlag: ConcurrentHashMap<String, String> = ConcurrentHashMap<String, String>()
        private var args: Array<String> = emptyArray()


        @Throws(IOException::class)
        fun load(args: Array<String>) {
            if (args.size == 0) {
                return
            }
            var spl: List<String>? = null
            var i = 0
            while (i < args.size) {
                val s = args[i]
                ++i
                if (s != null) {
                    spl = s.split("=").toList()
                }
                if (spl!!.size != 2) {
                    throw IOException(Intrinsics.stringPlus("Flag input is not valid. fag = ", s as Any?))
                }
                mapFlag[spl!![0]] = spl!![1]
            }
            Companion.args = (args)
        }

        fun getArgs(): Array<String> {
            return args
        }


        fun getFlag(flagName: String, flagDefault: String): String {
            return mapFlag.getOrDefault(flagName,flagDefault)
        }

        fun getFlag(flagName: String): String? {
            if (mapFlag.containsKey(flagName)) {
                return mapFlag[flagName]
            }
            println(Intrinsics.stringPlus("Flag is not exist. flagName = ", flagName as Any) as Any)
            System.exit(0)
            return null
        }

        fun getFlagToInt(flagName: String): Int {
            Intrinsics.checkNotNullParameter(flagName as Any, "flagName")
            val flag = getFlag(flagName)
            return flag?.toInt() ?: 0
        }

        fun getFlagToInt(flagName: String, flagDefault: Int): Int {
            return getFlag(flagName, flagDefault.toString()).toInt()
        }

        fun extendFlagSkipExist(flagName: String, flagValue: String) {
            if (mapFlag.containsKey(flagName)) {
                return
            }
            mapFlag[flagName] = flagValue
            var i = 0
            var `access$getArgs$cp`: Array<String> = args
            val n = (`access$getArgs$cp`?.size ?: 0) + 1
            val array = arrayOfNulls<String>(n)
            while (i < n) {
                array[i] = "$flagName=$flagValue"
                ++i
            }
            var `access$getArgs$cp2`: Array<String> = args
            if (`access$getArgs$cp2` != null) {
                val n2 = 0
                System.arraycopy(args, 0, array, 0, `access$getArgs$cp2`.size)
            }
            args = (array) as Array<String>
        }

    }
}