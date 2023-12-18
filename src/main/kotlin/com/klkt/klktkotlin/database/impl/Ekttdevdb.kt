package com.klkt.klktkotlin.database.impl

import com.klkt.klktkotlin.database.rdbms.RDBMSDataContext

open class Ekttdevdb : RDBMSDataContext() {

    init {
        this.dbName = "db_oracle_e_ktt_dev"
        this.dbType = "oracle"
        this.dbKey = "dbKttDev"
    }
//    @Autowired
//    constructor(rdbmsDataSource: RDBMSDataSource) : super(rdbmsDataSource) {
//        this.dbName = "db_oracle_e_ktt_dev"
//        this.dbType = "oracle"
//        this.dbKey = "dbKttDev"
//    }

}