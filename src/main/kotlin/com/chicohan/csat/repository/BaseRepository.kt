package com.chicohan.csat.repository

import com.chicohan.csat.model.Branch
import com.chicohan.csat.model.Counter
import com.chicohan.csat.model.Feedback
import com.chicohan.csat.model.Region
import com.chicohan.csat.model.Staff
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional


@Repository
interface RegionRepo : JpaRepository<Region, Long>
@Repository
interface BranchRepo : JpaRepository<Branch,Long>

@Repository
interface CounterRepository: JpaRepository<Counter,Long>{}

@Repository
interface  StaffRepo: JpaRepository<Staff,String>{
    fun findStaffById(staffId:String):Optional<Staff>
}

@Repository
interface FeedbackRepo:JpaRepository<Feedback,Long>{
    fun findFeedbackByRating(rating:Int):List<Feedback>
    fun findFeedBackByRatingAndBranchId(rating: Int, branchId: Long): List<Feedback>
    fun findByBranchId(branchId: Long): List<Feedback>
}