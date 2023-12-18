package com.klkt.klktkotlin.database.rdbms

import com.zaxxer.hikari.HikariConfig

class RDBMSDataSourceInfo {
    var dbType: String = ""
    var dbName: String = ""
    var driverClassName: String = "oracle.jdbc.OracleDriver"
    var jdbcUrl: String = "jdbc:oracle:thin:@//x.x.x.x:1521/db"
    var username: String = ""
    var password: String = ""
    var isAutoCommit: Boolean = false
    var minimumIdle: Int = 5
    var maximumPoolSize: Int = 50
    var poolName: String = "unknown"
    var connectionTimeout: Long = 600000L
    var maxLifetime: Long = 60000L
    var cachePrepStmts: Boolean = true
    var prepStmtCacheSize: Int = 250
    var prepStmtCacheSqlLimit: Int = 2048
    var maxRowGet: Int = 1000000


    fun getHikariConfig(): HikariConfig {
        val hikariConfig = HikariConfig()
        hikariConfig.driverClassName = driverClassName
        hikariConfig.jdbcUrl = jdbcUrl
        hikariConfig.username = username
        hikariConfig.password = password
        hikariConfig.isAutoCommit = isAutoCommit
        hikariConfig.minimumIdle = minimumIdle
        hikariConfig.maximumPoolSize = maximumPoolSize
        hikariConfig.poolName = poolName
        hikariConfig.connectionTimeout = connectionTimeout
        hikariConfig.maxLifetime = maxLifetime

        hikariConfig.addDataSourceProperty("cachePrepStmts", cachePrepStmts.toString())
        hikariConfig.addDataSourceProperty("prepStmtCacheSize", prepStmtCacheSize.toString())
        hikariConfig.addDataSourceProperty("prepStmtCacheSqlLimit", prepStmtCacheSqlLimit.toString())
        return hikariConfig
    }
}