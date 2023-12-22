package com.klkt.klktkotlin.database.impl

import com.klkt.klktkotlin.database.rdbms.RDBMSDataContext

open class Ekttdevdb : RDBMSDataContext() {

    init {
        this.dbName = this.propYAMLFlatMap["datasources.dbKttDev.dbName"].toString()
        this.dbType = this.propYAMLFlatMap["datasources.dbKttDev.dbType ="].toString()
        this.dbKey = this.propYAMLFlatMap["datasources.dbKttDev.dbKey"].toString()
        this.poolName = this.propYAMLFlatMap["datasources.dbKttDev.poolName"].toString()
    }
//    @Autowired
//    constructor(rdbmsDataSource: RDBMSDataSource) : super(rdbmsDataSource) {
//        this.dbName = "db_oracle_e_ktt_dev"
//        this.dbType = "oracle"
//        this.dbKey = "dbKttDev"
//    }

}