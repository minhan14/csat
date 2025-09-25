package com.chicohan.csat.controller

import com.chicohan.csat.model.Branch
import com.chicohan.csat.model.Feedback
import com.chicohan.csat.model.Staff
import com.chicohan.csat.repository.BranchRepo
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
        val staffList: List<Staff> = staffRepo.findAll()
        return ResponseEntity.ok(staffList)
    }

    @PostMapping
    fun createStaff(@RequestBody staff: Staff): ResponseEntity<Staff> {
        val savedStaff = staffRepo.save(staff)
        return ResponseEntity.ok(savedStaff)
    }
}

@RestController
@RequestMapping("api/branches")
class BranchController(private val branchRepo: BranchRepo) {

    @GetMapping
    fun getAllBranches(): ResponseEntity<List<BranchDto>> {
        val branches: List<BranchDto> = branchRepo.findAll().map {
            BranchDto(it.id, it.name, it?.region?.name)
        }
        return ResponseEntity.ok(branches)
    }

//    @PostMapping
//    fun createBranch(@RequestBody branch: Branch): ResponseEntity<Branch> {
//        val branchs = branchRepo.save(branch)
//        return ResponseEntity.ok(branchs)
//    }
}

data class BranchDto(
    val id: Long,
    val name: String,
    val regionName: String?
)
