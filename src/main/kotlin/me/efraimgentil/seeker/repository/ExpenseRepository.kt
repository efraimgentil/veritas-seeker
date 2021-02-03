package me.efraimgentil.seeker.repository

import me.efraimgentil.seeker.domain.PollingExpense
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface ExpenseRepository : JpaRepository<PollingExpense, Long> {
    fun findByHash(hash : String) : PollingExpense?
}