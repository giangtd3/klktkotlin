package com.klkt.klktkotlin.repo

import com.klkt.klktkotlin.database.impl.EGplxTtshdb
import org.springframework.stereotype.Repository

@Repository
class GplxTtshRepo:EGplxTtshdb() {
   fun checkConn() {
        try {
            this.logger.info("Conn: {}", this.getConn())
        } catch (e: Exception) {
            this.logger.error("ERROR:{}", e)
        }
   }
}