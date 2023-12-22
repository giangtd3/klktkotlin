package com.klkt.klktkotlin

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.klkt.klktkotlin.utils.FileUtils
import com.klkt.klktkotlin.utils.FlagUtils
import com.klkt.klktkotlin.utils.KLKTUtils
import com.klkt.klktkotlinapi.klktutils.GlobalUtils
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.web.servlet.config.annotation.EnableWebMvc
import springfox.documentation.swagger2.annotations.EnableSwagger2
import java.io.File
import java.time.LocalDateTime
import java.util.*
import java.util.function.Consumer


@EnableWebMvc
@EnableSwagger2
@SpringBootApplication
class KlktkotlinApplication {
    companion object {
        private val logger = LoggerFactory.getLogger(KlktkotlinApplication::class.java)
        private val spring_config_location = "--spring.config.location"
        private val log_config = "--logger.config"

        @JvmStatic
        fun main(args: Array<String>) {
            GlobalUtils.configLogger(
                    FlagUtils.getFlag(
                            log_config,
                            "etc/log.xml"
                    )
            )
            logger.info("Starting KTKL: {}", LocalDateTime.now())
            GlobalUtils.argsLoad(args)
            FlagUtils.extendFlagSkipExist(
                    spring_config_location,
                    FileUtils.userDir("etc/application.yml")
            )

            //runApplication<KlktkotlinApplication>(*args)
            val app: ApplicationContext = runApplication<KlktkotlinApplication>(*FlagUtils.getArgs())
            logger.info("HELLO MR.KHOAI!")
            val env: Environment = app.environment
            logger.info("Active profiles: {}", env.activeProfiles.contentToString())

            val mapResult = KLKTUtils.flattenedYaml("etc/application.yml", null)
            logger.info("mapResult: {}", mapResult)
            logger.info("datasources.dbKttTest.dbKey:{}", mapResult["datasources.dbKttTest.dbKey"])
            logger.info("datasources.dbKttTest.dbType:{}", mapResult["datasources.dbKttTest.dbType"])
            logger.info("datasources.dbKttTest.poolName:{}", mapResult["datasources.dbKttTest.poolName"])
            logger.info("datasources.dbKttTest.dbName:{}", mapResult["datasources.dbKttTest.dbName"])

            val mapResult2 = KLKTUtils.flattenedListYaml("etc/application.yml", null)
            logger.info("mapResult2: {}", mapResult2)

        }


    }

}

