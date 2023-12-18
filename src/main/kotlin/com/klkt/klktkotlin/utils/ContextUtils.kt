package com.klkt.klktkotlin.utils

import org.springframework.beans.BeansException
import org.springframework.context.ApplicationContext
import org.springframework.context.ApplicationContextAware
import org.springframework.stereotype.Component

@Component
class ContextUtils: ApplicationContextAware  {

    companion object {
        private lateinit var applicationContext: ApplicationContext
        fun <T> getBean(clazz: Class<T>): T {
            return applicationContext!!.getBean(clazz)
        }

        fun getBean(name: String?): Any? {
            return applicationContext!!.getBean(name!!)
        }

        fun <T> getBean(clazz: Class<T>, name: String?): T {
            return applicationContext!!.getBean(name!!, clazz)
        }
    }
    @Throws(BeansException::class)
    override fun setApplicationContext(applicationContext: ApplicationContext) {
        ContextUtils.applicationContext = applicationContext
    }
}