package com.klkt.klktkotlin.database.impl

import com.klkt.klktkotlin.database.rdbms.RDBMSDataContext

open class Ettshdevdb: RDBMSDataContext()  {
    init {
        this.dbName = "mot_ttsh_dev"
        this.dbType = "postgres"
        this.dbKey = "dbttsh"
    }
}