package com.klkt.klktkotlin.database

import utils.KLKTJsonObject
import java.sql.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.Date
import kotlin.jvm.internal.Intrinsics

class RDBMSUtils {
    companion object {

        fun isSqlQuery(procedureOrSqlQuery: String): Boolean {
            return procedureOrSqlQuery.contains(" ")
        }

        fun buildStoreExec(procedureOrSqlQuery: String, paramSize: Int, resCursor: Boolean): String? {

            if (procedureOrSqlQuery.contentEquals("(")) {
                return "{call $procedureOrSqlQuery }"
            }
            val br = StringBuffer()
            if (resCursor) {
                br.append("?")
            }
            var j = 0
            if (j < paramSize) {
                do {
                    val i = j
                    ++j
                    if (br.isNotEmpty()) {
                        br.append(", ")
                    }
                    br.append("?")
                } while (j < paramSize)
            }
            return "{call " + procedureOrSqlQuery + '(' + br as Any + ") }"
        }

        fun <T : AutoCloseable> close(vararg obj: T) {

            var i = 0
            while (i < obj.size) {
                val item: AutoCloseable = obj[i]
                ++i
                try {
                    if (item is Connection) {
                        item.rollback()
                    }
                } catch (e: java.lang.Exception) {
                    println(Intrinsics.stringPlus("rollback error: ", e as Any) as Any)
                }
                try {
                    item.close()
                } catch (e: java.lang.Exception) {
                    println(Intrinsics.stringPlus("close error: ", e as Any) as Any)
                }
            }
        }

        fun mapValue(obj: Any?): Any? {
            try {
                if (obj == null) {
                    return obj
                }
                if (obj is java.sql.Date) {
                    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.getDefault())
                    val dateVal = Date(obj.time)
                    return df.format(dateVal)
                }
                if (obj is Timestamp) {
                    val df = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
                    val dateVal = Date(obj.time)
                    return df.format(dateVal)
                }
                if (obj is Clob || obj is NClob) {
                    val objClob = obj as Clob
                    return objClob.getSubString(1L, objClob.length().toInt())
                }
            } catch (e: Exception) {
                println(e)
            }
            return obj
        }

        fun addParam(pre: CallableStatement, vararg params: Any) {

            val startIdx = 1
            if (params.isEmpty()) {
                return
            }
            if (params[0] is Array<*>) {
                val lst = params[0] as Array<*>
                var k = 0
                val n = lst.size - 1
                if (k <= n) {
                    do {
                        val i = k
                        ++k
                        pre.setObject(i + startIdx, lst[i])
                    } while (k <= n)
                }
                return
            }
            var l = 0
            val n2 = params.size - 1
            if (l <= n2) {
                do {
                    val j = l
                    ++l
                    pre.setObject(j + startIdx, params[j])
                } while (l <= n2)
            }
        }

        fun parseResultSetData(re: ResultSet?, result: KLKTJsonObject) {

            if (re == null || !re.isBeforeFirst) {
                return
            }
            val hInfo = re.metaData
            while (re.next()) {
                val row: KLKTJsonObject = KLKTJsonObject.newObj()
                var n = 1
                val columnCount = hInfo.columnCount
                if (n <= columnCount) {
                    var i: Int
                    do {
                        i = n
                        ++n
                        row.put(hInfo.getColumnName(i), mapValue(re.getObject(hInfo.getColumnName(i))))
                    } while (i != columnCount)
                }
                result.put(row)
            }
        }
    }
}