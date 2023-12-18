package com.klkt.klktkotlin.database.rdbms

interface IRDBMSDataContext {

    fun query(procedureOrSqlQuery: String, vararg params: Any, clazz: Class<Any>): Any

    fun query(procedureOrSqlQuery: String, vararg params: Any): Any

    fun queryWithCommit(procedureOrSqlQuery: String, vararg params: Any): Any

    fun execute(procedureOrSqlQuery: String, vararg params: Any)

    fun executeWithCommit(procedureOrSqlQuery: String, vararg params: Any)
}