package com.klkt.klktkotlin.database.impl

import com.klkt.klktkotlin.database.rdbms.RDBMSDataContext

open class Ettshdevdb: RDBMSDataContext()  {
    init {
        this.dbName = this.propYAMLFlatMap["datasources.dbttsh.dbName"].toString()
        this.dbType = this.propYAMLFlatMap["datasources.dbttsh.dbType ="].toString()
        this.dbKey = this.propYAMLFlatMap["datasources.dbttsh.dbKey"].toString()
        this.poolName = this.propYAMLFlatMap["datasources.dbttsh.poolName"].toString()
    }
}