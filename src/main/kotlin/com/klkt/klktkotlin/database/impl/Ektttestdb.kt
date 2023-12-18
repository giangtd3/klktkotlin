package com.klkt.klktkotlin.database.impl

import com.klkt.klktkotlin.database.RDBMSDataContext

 open class Ektttestdb: RDBMSDataContext() {
    init {
        this.dbName = "db_oracle_e_ktt_test"
        this.dbType = "oracle"
        this.dbKey = "dbKttTest"
    }

//    @Autowired
//    constructor(rdbmsDataSource: RDBMSDataSource) : super(rdbmsDataSource) {
//        this.dbName = "db_oracle_e_ktt_test"
//        this.dbType = "oracle"
//        this.dbKey = "dbKttTest"
//    }
}