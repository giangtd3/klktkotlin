package com.klkt.klktkotlin.webapi.auth.service

import com.klkt.klktkotlin.webapi.auth.model.KLKTUser
import com.klkt.klktkotlin.webapi.auth.repo.KLKTUserRepo
import org.springframework.stereotype.Service
import java.util.*

@Service
class UserService(private val userRepo: KLKTUserRepo) {
    fun createUser(user: KLKTUser): KLKTUser? {
        val found = userRepo.findByUsername(user.username)

        return if (found == null) {
            userRepo.save(user)
            user
        } else null
    }

    fun findByUUID(uuid: UUID): KLKTUser? =
        userRepo.findByUUID(uuid)

    fun findAll(): List<KLKTUser> =
        userRepo.findAll()
            .toList()

    fun deleteByUUID(uuid: UUID): Boolean = userRepo.deleteByUUID(uuid)
}