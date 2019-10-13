package me.efraimgentil.seeker.repository

import me.efraimgentil.seeker.domain.PollingExpense
import org.springframework.data.jpa.repository.JpaRepository

interface ExpenseRepository : JpaRepository<PollingExpense, Long> {
    fun findByDocumentId(documentId : Long) : PollingExpense
}