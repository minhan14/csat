package com.chicohan.csat.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"])
class Region(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String
)

@JsonIgnoreProperties(value = ["hibernateLazyInitializer", "handler"])
@Entity
class Branch(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "region_id")
    val region: Region
)

@Entity
class Counter(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val counterNumber: Int,
    @ManyToOne
    @JoinColumn(name = "branch_id")
    val branch: Branch
)

@Entity
class Staff(
    @Id
    @Column(name = "staff_id")
    val id: String,
    val name: String,
    @Column(nullable = false)
    val password: String
)

@Entity
class Feedback(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id:Long = 0,
    @Column(nullable = false)
    val rating:Int,
    @Column(columnDefinition = "TEXT", nullable = true)
    val comment:String?,
    @Column(nullable = true)
    val category:String?,
    @Column(nullable = false)
    val createdAt:LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "staff_id")
    val staff:Staff,
    @ManyToOne
    @JoinColumn(name = "branch_id")
    val branch:Branch,
    @ManyToOne
    @JoinColumn(name = "counter_id")
    val counter:Counter
)
