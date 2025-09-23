package com.chicohan.csat

import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.util.Base64

@SpringBootApplication
class CsatApplication

fun main(args: Array<String>) {
	runApplication<CsatApplication>(*args)
}
