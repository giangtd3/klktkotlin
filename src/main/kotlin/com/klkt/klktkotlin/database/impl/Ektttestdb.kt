package com.klkt.klktkotlin.database.impl

import com.klkt.klktkotlin.database.rdbms.RDBMSDataContext

 open class Ektttestdb: RDBMSDataContext() {
    init {
        this.dbName = this.propYAMLFlatMap["datasources.dbKttTest.dbName"].toString()
        this.dbType = this.propYAMLFlatMap["datasources.dbKttTest.dbType ="].toString()
        this.dbKey = this.propYAMLFlatMap["datasources.dbKttTest.dbKey"].toString()
        this.poolName = this.propYAMLFlatMap["datasources.dbKttTest.poolName"].toString()
    }
}