package com.klkt.klktkotlin.webapi.auth.service

import com.klkt.klktkotlin.webapi.auth.repo.KLKTUserRepo
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
typealias ApplicationUser = com.klkt.klktkotlin.webapi.auth.model.KLKTUser
@Service
class CustomUserDetailsService(private val userRepo: KLKTUserRepo) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails =
        userRepo.findByUsername(username)
            ?.mapToUserDetails()
            ?: throw UsernameNotFoundException("User Not Found with username: $username !!!")

    private fun ApplicationUser.mapToUserDetails(): UserDetails =
        User.builder()
            .username(this.username)
            .password(this.password)
            .roles(this.role.name)
            .build()
}