package com.chicohan.csat.security

import com.fasterxml.jackson.datatype.hibernate6.Hibernate6Module
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JacksonConfig {
    @Bean
    fun hibernateModule(): Hibernate6Module{
        return Hibernate6Module()
    }
}
