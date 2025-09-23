package com.chicohan.csat.controller

import com.chicohan.csat.security.JwtUtils
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


data class LoginRequest(val staffId: String,val password: String)
data class LoginResponse(val token: String)

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userDetailsService: UserDetailsService,
    private val jwtUtil: JwtUtils
) {

    @PostMapping("/login")
    fun createAuthenticationToken(@RequestBody loginRequest: LoginRequest): ResponseEntity<*> {
        authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(loginRequest.staffId, loginRequest.password)
        )
        val userDetails = userDetailsService.loadUserByUsername(loginRequest.staffId)
        val jwt = jwtUtil.generateToken(userDetails)

        return ResponseEntity.ok(LoginResponse(jwt))
    }
}