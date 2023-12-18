package com.klkt.klktkotlin.database.rdbms

import com.zaxxer.hikari.HikariDataSource
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration
import java.util.concurrent.ConcurrentHashMap
import javax.annotation.PostConstruct


@Configuration
@ConfigurationProperties(prefix = "")
@EnableAutoConfiguration
@EnableConfigurationProperties
class RDBMSDataSource {
    val datasources: Map<String, RDBMSDataSourceInfo> = HashMap()
    val dataPoolMap: ConcurrentHashMap<String, HikariDataSource> = ConcurrentHashMap()
//    companion object {
//        val mapDb: HashMap<String, HikariDataSource> = HashMap()
//    }

    @PostConstruct
    private fun setDataSource() {
        if (dataPoolMap.isEmpty()) {
            for ((key, value) in datasources) {
                dataPoolMap[key] = HikariDataSource(value.getHikariConfig())
            }
        }
    }
}