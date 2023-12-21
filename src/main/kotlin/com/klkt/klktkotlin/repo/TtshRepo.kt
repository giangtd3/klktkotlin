package com.klkt.klktkotlin.repo

import com.klkt.klktkotlin.database.impl.Ettshdevdb
import org.springframework.stereotype.Repository
import KLKTJavaUtils.KLKTJsonObject

@Repository
class TtshRepo:Ettshdevdb() {
    fun getData(): KLKTJsonObject {
        var rs: KLKTJsonObject = KLKTJsonObject.newArr()
        try {
            rs = this.queryWithCommit(
                    "pkg_auth_user___findbyid",
                    "cuongtm"
            ) as KLKTJsonObject
        } catch (e: Exception) {
            this.logger.error("ERROR:", e.printStackTrace())
        }

        return rs
    }
}