package com.klkt.klktkotlin.webapi.auth.model

import java.util.*

data class KLKTUser (
    val id: UUID,
    val username: String,
    val password: String,
    val role: Role
)
enum class Role {
    USER, ADMIN
}