package me.efraimgentil.seeker.repository

import me.efraimgentil.seeker.domain.Expense
import org.springframework.data.jpa.repository.JpaRepository

interface ExpenseRepository : JpaRepository<Expense, Long> {
    fun findByHash(hash : String) : Expense?
}