package com.klkt.klktkotlin.repo

import com.klkt.klktkotlin.database.impl.Ektttestdb
import utils.KLKTJsonObject
import org.springframework.stereotype.Repository

@Repository
class DvcTestRepo: Ektttestdb() {
    fun getData(): KLKTJsonObject {
        var rs: KLKTJsonObject = KLKTJsonObject.newArr()
        try {
            rs = this.query(
                "PKG_NMT___TRACUUGPLXCAPQUOCTE.SP_SEARCH_BY_SO_GPLX_AND_NOI_CAP_GPLX",
                "P_SO_GPLX",
                "P_NOI_CAP_GPLX"
            ) as KLKTJsonObject
        } catch (e: Exception) {
            this.logger.error("ERROR:", e.printStackTrace())
        }

        return rs
    }
}