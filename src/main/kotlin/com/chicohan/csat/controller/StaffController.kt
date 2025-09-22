package com.chicohan.csat.controller

import com.chicohan.csat.model.Feedback
import com.chicohan.csat.model.Staff
import com.chicohan.csat.repository.StaffRepo
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController


@RestController
@RequestMapping("api/staffs")
class StaffController(private val staffRepo: StaffRepo) {

    @GetMapping
    fun getAllStaffs(): ResponseEntity<List<Staff>> {
        val staffList: List<Staff>  = staffRepo.findAll()
        return ResponseEntity.ok(staffList)
    }

    @PostMapping
    fun createStaff(@RequestBody staff: Staff): ResponseEntity<Staff>{
        val savedStaff = staffRepo.save(staff)
        return ResponseEntity.ok(savedStaff)
    }

}