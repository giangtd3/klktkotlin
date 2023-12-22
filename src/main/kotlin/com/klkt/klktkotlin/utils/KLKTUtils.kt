package com.klkt.klktkotlin.utils

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import org.slf4j.LoggerFactory
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.io.FileSystemResource
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.util.*
import java.util.function.Consumer


class KLKTUtils {
    companion object {
        private val logger = LoggerFactory.getLogger(KLKTUtils::class.java)
        fun propertySourcesPlaceholderConfigurer(): PropertySourcesPlaceholderConfigurer? {
            val properties = PropertySourcesPlaceholderConfigurer()
            properties.setLocation(FileSystemResource("etc/application.yml"))
            properties.setIgnoreResourceNotFound(false)
            return properties
        }

        @Throws(IOException::class)
        fun readPropertiesFile(fileName: String?): Properties? {
            var fis: FileInputStream? = null
            var prop: Properties? = null
            try {
                fis = FileInputStream(fileName)
                prop = Properties()
                prop.load(fis)
            } catch (fnfe: FileNotFoundException) {
                fnfe.printStackTrace()
            } catch (ioe: IOException) {
                ioe.printStackTrace()
            } finally {
                fis!!.close()
            }
            return prop
        }

        fun flattenedYaml(fileName: String, parentKey: String?): MutableMap<String, Any?> {
            val mapper = ObjectMapper(YAMLFactory())
            val file = File("etc/application.yml")
            val filePath: String = file.getPath()
            logger.info("file: {}", filePath)
            val mapLoaded: Map<String, Any>? = mapper.readValue(file, Map::class.java) as Map<String, Any>?
            val mapResult: MutableMap<String, Any?> = HashMap(20)
            this.flattenedYaml(mapLoaded, mapResult, null)
            return mapResult
        }

        fun flattenedYaml(inputMap: Map<String, Any>?, resultMap: MutableMap<String, Any?>, parentKey: String?) {
            //recursion exit conditon
            if (inputMap.isNullOrEmpty()) {
                return
            }
            inputMap.keys.forEach(Consumer { x: String ->
                val keyBeingProcessed: String
                if (parentKey != null && resultMap.containsKey(parentKey)) {
                    val currentKey = "$parentKey.$x"
                    resultMap[currentKey] = null
                    keyBeingProcessed = currentKey
                } else {
                    resultMap[x] = null
                    keyBeingProcessed = x
                }
                val o = inputMap[x]
                resultMap[keyBeingProcessed] = o
                if (o is Map<*, *>) {
                    flattenedYaml(inputMap[x] as Map<String, Any>?, resultMap, keyBeingProcessed)
                }
            })
        }

        /*
        *myapp:
          tables:
            - table1:
                name: Test1
                keys:
                  - key1:
                      - key10
                      - key10

                  - key2:
                      key2_count: 9
                      key2_name: test

            - table2:
                name: Test1
                keys:
                  - key3
                  - key4
        ==> output:
        *myapp.tables.[0].table2.name-->Test1
        myapp.tables.[0].table1.keys.[0].key1-->[key10, key10]
        myapp.tables.[0].table1.keys.[0].key2-->{key2_count=9, key2_name=test}
        myapp.tables-->[{table1={name=Test1, keys=[{key1=[key10, key10]}, {key2={key2_count=9, key2_name=test}}]}}, {table2={name=Test1, keys=[key3, key4]}}]
        myapp.tables.[0].table1.name-->Test1
        myapp.tables.[0].table1.keys.[0]-->null
        myapp.tables.[0].table1.keys.[0].key2.key2_name-->test
        myapp-->{tables=[{table1={name=Test1, keys=[{key1=[key10, key10]}, {key2={key2_count=9, key2_name=test}}]}}, {table2={name=Test1, keys=[key3, key4]}}]}
        myapp.tables.[0].table1.keys-->[{key1=[key10, key10]}, {key2={key2_count=9, key2_name=test}}]
        myapp.tables.[0]-->null
        myapp.tables.[0].table2.keys-->[key3, key4]
        myapp.tables.[0].table2-->{name=Test1, keys=[key3, key4]}
        myapp.tables.[0].table1-->{name=Test1, keys=[{key1=[key10, key10]}, {key2={key2_count=9, key2_name=test}}]}
        myapp.tables.[0].table1.keys.[0].key2.key2_count-->9
        *
        * */

        fun flattenedListYaml(fileName: String, parentKey: String?): MutableMap<String, Any?> {
            val mapper = ObjectMapper(YAMLFactory())
            val file = File("etc/application.yml")
            val filePath: String = file.getPath()
            logger.info("file: {}", filePath)
            val mapLoaded: Map<String, Any>? = mapper.readValue(file, Map::class.java) as Map<String, Any>?
            val mapResult: MutableMap<String, Any?> = HashMap(20)
            this.flattenedListYaml(mapLoaded, mapResult, null)
            return mapResult
        }
        private fun flattenedListYaml(inputMap: Map<String, Any>?, resultMap: MutableMap<String, Any?>, parentKey: String?) {
            //recursion exit conditon
            if (inputMap == null || inputMap.size == 0) {
                return
            }
            inputMap.keys.forEach(Consumer { x: String ->
                val keyBeingProcessed: String
                if (parentKey != null && resultMap.containsKey(parentKey)) {
                    val currentKey = "$parentKey.$x"
                    resultMap[currentKey] = null
                    keyBeingProcessed = currentKey
                } else {
                    resultMap[x] = null
                    keyBeingProcessed = x
                }
                val o = inputMap[x]
                resultMap[keyBeingProcessed] = o
                if (o is Map<*, *>) {
                    flattenedYaml(inputMap[x] as Map<String, Any>?, resultMap, keyBeingProcessed)
                } else if (o is List<*>) {
                    val counter = 0
                    for (obj in o) {
                        if (obj is Map<*, *>) {
                            val currentKey = "$keyBeingProcessed.[$counter]"
                            resultMap[currentKey] = null
                            flattenedYaml(obj as Map<String, Any>?, resultMap, currentKey)
                        }
                    }
                }
            })
        }

    }
}