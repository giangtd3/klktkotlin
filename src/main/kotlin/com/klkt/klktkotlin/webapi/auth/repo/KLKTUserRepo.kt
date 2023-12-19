package com.klkt.klktkotlin.webapi.auth.repo

import com.klkt.klktkotlin.webapi.auth.model.KLKTUser
import com.klkt.klktkotlin.webapi.auth.model.Role
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class KLKTUserRepo(private val encoder: PasswordEncoder) {
    private val users = mutableSetOf(
        KLKTUser(
            id = UUID.randomUUID(),
            username = "email-1@gmail.com",
            password = encoder.encode("pass1"),
            role = Role.USER,
        ),
        KLKTUser(
            id = UUID.randomUUID(),
            username = "email-2@gmail.com",
            password = encoder.encode("pass2"),
            role = Role.ADMIN,
        ),
        KLKTUser(
            id = UUID.randomUUID(),
            username = "email-3@gmail.com",
            password = encoder.encode("pass3"),
            role = Role.USER,
        ),
    )

    fun save(user: KLKTUser): Boolean {
        val updated = user.copy(password = encoder.encode(user.password))

        return users.add(updated)
    }

    fun findByUsername(username: String): KLKTUser? =
        users
            .firstOrNull { it.username == username }

    fun findAll(): Set<KLKTUser> =
        users

    fun findByUUID(uuid: UUID): KLKTUser? =
        users
            .firstOrNull { it.id == uuid }

    fun deleteByUUID(uuid: UUID): Boolean {
        val foundUser = findByUUID(uuid)

        return foundUser?.let {
            users.removeIf {
                it.id == uuid
            }
        } ?: false
    }
}