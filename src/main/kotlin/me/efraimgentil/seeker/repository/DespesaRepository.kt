package me.efraimgentil.seeker.repository

import me.efraimgentil.seeker.domain.Despesa
import org.springframework.data.jpa.repository.JpaRepository

interface DespesaRepository : JpaRepository<Despesa, Long> {
    fun findByDocumentId(documentId : Long) : Despesa
}