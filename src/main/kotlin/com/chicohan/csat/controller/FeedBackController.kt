package com.chicohan.csat.controller

import com.chicohan.csat.model.Feedback
import com.chicohan.csat.repository.BranchRepo
import com.chicohan.csat.repository.CounterRepository
import com.chicohan.csat.repository.FeedbackRepo
import com.chicohan.csat.repository.StaffRepo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDateTime


data class FeedbackRequest(
    val rating: Int,
    val comment: String?,
    val category: String?,
    val branchId: Long,
    val counterId: Long,
    val staffId: String
)

data class FeedbackResponse(
    val id: Long,
    val rating: Int,
    val comment: String?,
    val category: String?,
    val createdAt: LocalDateTime,
    val staffId: String,
    val branchName: String,
    val counterNumber: Int
)

@RestController
@RequestMapping("api/feedback")
class FeedBackController(
    private val feedbackRepo: FeedbackRepo,
    private val staffRepo: StaffRepo,
    private val branchRepo: BranchRepo,
    private val counterRepository: CounterRepository
){

    @PostMapping
    fun submitFeedback(
        @RequestBody feedbackRequest: FeedbackRequest
    ): ResponseEntity<FeedbackResponse>{
        val staff = staffRepo.findStaffById(feedbackRequest.staffId).orElse(null) ?: return ResponseEntity.status(
            HttpStatus.UNAUTHORIZED).build()
        val branch = branchRepo.findById(feedbackRequest.branchId).orElse(null)
            ?: return ResponseEntity.badRequest().build()

        val counter = counterRepository.findById(feedbackRequest.counterId).orElse(null)
            ?: return ResponseEntity.badRequest().build()

        if (feedbackRequest.rating !in 1..5) {
            return ResponseEntity.badRequest().build()
        }

        val feedback = Feedback(
            rating = feedbackRequest.rating,
            comment = feedbackRequest.comment,
            category = feedbackRequest.category,
            staff = staff,
            branch = branch,
            counter = counter
        )
        val savedFeedback = feedbackRepo.save(feedback)

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToFeedbackResponse(savedFeedback))

    }
    @GetMapping
    fun getAllFeedback(
        @RequestParam(required = false) rating: Int?,
        @RequestParam(required = false) branchId: Long?
    ): ResponseEntity<List<FeedbackResponse>> {
        val feedbackList: List<Feedback> = when {
            rating != null && branchId != null -> feedbackRepo.findFeedBackByRatingAndBranchId(rating, branchId)
            rating != null -> feedbackRepo.findFeedbackByRating(rating)
            branchId != null -> feedbackRepo.findByBranchId(branchId)
            else -> feedbackRepo.findAll()
        }

        val response = feedbackList.map { mapToFeedbackResponse(it) }
        return ResponseEntity.ok(response)
    }
    private fun mapToFeedbackResponse(feedback: Feedback): FeedbackResponse {
        return FeedbackResponse(
            id = feedback.id,
            rating = feedback.rating,
            comment = feedback.comment,
            category = feedback.category,
            createdAt = feedback.createdAt,
            staffId = feedback.staff.id,
            branchName = feedback.branch.name,
            counterNumber = feedback.counter.counterNumber
        )
    }
}