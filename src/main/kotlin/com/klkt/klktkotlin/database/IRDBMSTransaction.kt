package com.klkt.klktkotlin.database

import java.sql.Connection
import java.sql.ResultSet

interface IRDBMSTransaction {
    fun getConn(): Connection

    fun query(p0: String, vararg p1: Any): ResultSet
    fun execute(p0: String, vararg p1: Any)
    fun executeBatch(p0: String, vararg p1: List<*>): IntArray?
    fun commit()
    fun rollback()
}