package com.klkt.klktkotlin.database.rdbms

import com.klkt.klktkotlin.utils.ContextUtils
import KLKTJavaUtils.KLKTJsonObject
import com.klkt.klktkotlin.utils.KLKTUtils
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.sql.CallableStatement
import java.sql.Connection
import java.sql.ResultSet
import java.sql.SQLException

abstract class RDBMSDataContext : IRDBMSDataContext {
    protected val logger: Logger = LoggerFactory.getLogger(this.javaClass as Class<*>)
    protected var propYAMLFlatMap: MutableMap<String, Any?> = HashMap()
    private val FILE_NAME_PROP = "etc/application.yml"

    protected lateinit var dbKey: String
    protected lateinit var dbName: String
    protected lateinit var dbType: String
    protected lateinit var poolName: String
    private lateinit var dbConn: Connection

    init {
        this.propYAMLFlatMap = KLKTUtils.flattenedYaml(FILE_NAME_PROP, null)
    }

    @Synchronized
    protected fun getConn(): Connection {
        try {
            return ContextUtils.getBean(RDBMSDataSource::class.java).dataPoolMap[this.dbKey]?.connection as Connection
        } catch (ex: Exception) {
            val message = ex.message
            if (message == null || message.contains("Timed out waiting for a free available connection")) {
                logger!!.error("[DB_ERROR] Can't get connection. Refresh pool......")
                return ContextUtils.getBean(RDBMSDataSource::class.java).dataPoolMap[this.dbKey]?.connection as Connection

            }
            throw SQLException("Get Connection err!!! ", ex)
        }
    }

    override fun query(procedureOrSqlQuery: String, vararg params: Any, clazz: Class<Any>): Any {
        TODO("Not yet implemented")
    }

    override fun query(procedureOrSqlQuery: String, vararg params: Any): Any {
        lateinit var re: ResultSet
        lateinit var pre: CallableStatement
        lateinit var conn: Connection
        val result: KLKTJsonObject = KLKTJsonObject.newArr()
        try {
            conn = this.getConn()
            if (RDBMSUtils.isSqlQuery(procedureOrSqlQuery)) {
                pre = conn.prepareCall(procedureOrSqlQuery)
                RDBMSUtils.addParam(pre, params)
                re = pre.executeQuery()
            } else {
                pre = conn.prepareCall(
                    RDBMSUtils.buildStoreExec(
                        procedureOrSqlQuery,
                        params.size,
                        true
                    )
                )
                pre.registerOutParameter(params.size + 1, -10)
                RDBMSUtils.addParam(pre, params)
                pre.execute()
                re = pre.getObject(params.size + 1) as ResultSet
            }
            RDBMSUtils.parseResultSetData(re, result)
        } finally {
            RDBMSUtils.close(re, pre, conn)
        }
        return result
    }

    override fun queryWithCommit(procedureOrSqlQuery: String, vararg params: Any): Any {
        lateinit var re: ResultSet
        lateinit var pre: CallableStatement
        lateinit var conn: Connection
        val result: KLKTJsonObject = KLKTJsonObject.newArr()
        try {
            conn = this.getConn()
            conn.autoCommit = false
            if (RDBMSUtils.isSqlQuery(procedureOrSqlQuery)) {
                pre = conn.prepareCall(procedureOrSqlQuery)
                RDBMSUtils.addParam(pre, params)
                re = pre.executeQuery()
                this.commit(conn)
            } else {
                val spText = RDBMSUtils.buildStoreExec(
                    procedureOrSqlQuery,
                    params.size,
                    true
                )
                logger.info("spText: {}", spText)

                pre = conn.prepareCall(spText)
                pre.registerOutParameter(params.size + 1, java.sql.Types.REF_CURSOR)
                RDBMSUtils.addParam(pre, params)
                pre.execute()
                this.commit(conn)
                re = pre.getObject(params.size + 1) as ResultSet
            }
            RDBMSUtils.parseResultSetData(re, result)
        } catch (e: Exception) {
            logger.error("queryWithCommit exception: {}", e)
        } finally {
            RDBMSUtils.close(re, pre, conn)
        }
        return result
    }

    override fun execute(procedureOrSqlQuery: String, vararg params: Any) {
        lateinit var pre: CallableStatement
        lateinit var conn: Connection
        try {
            conn = this.getConn()
            conn.autoCommit = false
            pre = if (RDBMSUtils.isSqlQuery(procedureOrSqlQuery)) conn.prepareCall(
                procedureOrSqlQuery
            ) else conn.prepareCall(
                RDBMSUtils.buildStoreExec(
                    procedureOrSqlQuery,
                    params.size,
                    false
                )
            )
            RDBMSUtils.addParam(pre, params)
            pre.execute()
            this.commit(conn)
        } catch (e: Exception) {
            this.rollback(conn)
            throw e
        } finally {
            RDBMSUtils.close(pre, conn)
        }
    }


    override fun executeWithCommit(procedureOrSqlQuery: String, vararg params: Any) {
        lateinit var pre: CallableStatement
        lateinit var conn: Connection
        try {
            conn = this.getConn()
            conn.autoCommit = false
            pre = if (RDBMSUtils.isSqlQuery(procedureOrSqlQuery)) conn.prepareCall(
                procedureOrSqlQuery
            ) else conn.prepareCall(
                RDBMSUtils.buildStoreExec(
                    procedureOrSqlQuery,
                    params.size,
                    false
                )
            )
            RDBMSUtils.addParam(pre, params)
            pre.execute()
            this.commit(conn)

        } catch (e: Exception) {
            this.rollback(conn)
            throw e
        } finally {
            RDBMSUtils.close(pre, conn)
        }
    }


    private fun commit(conn: Connection) {
        try {
            conn.commit()
        } catch (e: Exception) {
            throw e
        }
    }

    private fun rollback(conn: Connection) {
        try {
            conn.rollback()
        } catch (e: Exception) {
            throw e
        }
    }

}