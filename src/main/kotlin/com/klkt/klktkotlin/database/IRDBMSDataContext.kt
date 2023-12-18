package com.klkt.klktkotlin.database

import utils.KLKTJsonObject
import java.sql.Connection

interface IRDBMSDataContext {

    fun query(procedureOrSqlQuery: String, vararg params: Any): KLKTJsonObject

    fun queryWithCommit(procedureOrSqlQuery: String, vararg params: Any): KLKTJsonObject

    fun execute(procedureOrSqlQuery: String, vararg params: Any)

    fun executeWithCommit(procedureOrSqlQuery: String, vararg params: Any)
}