package com.chicohan.csat.service

import com.chicohan.csat.repository.StaffRepo
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import kotlin.jvm.Throws

@Service
class UserDetailServiceImpl(
    private val staffRepo: StaffRepo
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        val staff = staffRepo.findStaffById(username)
            .orElseThrow { UsernameNotFoundException("StaffNotFound $username") }
        return User.withUsername(staff.id)
            .password(staff.password)
            .authorities("STAFF")
            .build()

    }
}