package me.efraimgentil.seeker.repository

import me.efraimgentil.seeker.domain.PollingCongressman
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.util.*

interface PollingCongressmanRepository : JpaRepository<PollingCongressman , Int> {
    fun findByCongressmanId(congressmanId : Long) : Optional<PollingCongressman>

    @Query("select a from PollingDeputado a where a.lastPull <= :lastPull order by a.lastPull asc")
    fun findAllWithLastPullBefore(@Param("lastPull") lastPull: LocalDate) : Set<PollingCongressman>
}