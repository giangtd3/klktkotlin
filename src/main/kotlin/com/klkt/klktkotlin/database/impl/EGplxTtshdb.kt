package com.klkt.klktkotlin.database.impl

import com.klkt.klktkotlin.database.rdbms.RDBMSDataContext

open class EGplxTtshdb: RDBMSDataContext() {
    init {
        this.dbName = this.propYAMLFlatMap["datasources.gplxTtsh.dbName"].toString()
        this.dbType = this.propYAMLFlatMap["datasources.gplxTtsh.dbType"].toString()
        this.dbKey = this.propYAMLFlatMap["datasources.gplxTtsh.dbKey"].toString()
        this.poolName = this.propYAMLFlatMap["datasources.gplxTtsh.poolName"].toString()
    }
}