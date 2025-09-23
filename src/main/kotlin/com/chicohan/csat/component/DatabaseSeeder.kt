package com.chicohan.csat.component

import com.chicohan.csat.model.Branch
import com.chicohan.csat.model.Counter
import com.chicohan.csat.model.Region
import com.chicohan.csat.model.Staff
import com.chicohan.csat.repository.BranchRepo
import com.chicohan.csat.repository.CounterRepository
import com.chicohan.csat.repository.RegionRepo
import com.chicohan.csat.repository.StaffRepo
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.boot.CommandLineRunner
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder


@Component
class DatabaseSeeder(
    private val regionRepository: RegionRepo,
    private val branchRepository: BranchRepo,
    private val counterRepository: CounterRepository,
    private val staffRepository: StaffRepo,
    private val passwordEncoder: PasswordEncoder
) : CommandLineRunner {

    override fun run(vararg args: String?) {
        if (regionRepository.count() == 0L) {
            println("Seeding database with initial data...")

            val yangon = regionRepository.save(Region(name = "Yangon"))
            val mandalay = regionRepository.save(Region(name = "Mandalay"))

            val downtownBranch = branchRepository.save(Branch(name = "Downtown Branch", region = yangon))
            val uptownBranch = branchRepository.save(Branch(name = "Uptown Branch", region = mandalay))

            (1..5).forEach {
                counterRepository.save(Counter(counterNumber = it, branch = downtownBranch))
            }
            (1..3).forEach {
                counterRepository.save(Counter(counterNumber = it, branch = uptownBranch))
            }

            val staff1 = Staff(
                id = "S001",
                name = "John Smith",
                password = passwordEncoder.encode("password123")
            )
            val staff2 = Staff(
                id = "S002",
                name = "Jessi",
                password = passwordEncoder.encode("password456")
            )

            staffRepository.save(staff1)
            staffRepository.save(staff2)

            println("Database seeding complete.")
        }
    }
}