package me.efraimgentil.seeker.repository

import me.efraimgentil.seeker.client.dto.GetDeputadoDTO
import me.efraimgentil.seeker.domain.PollingDeputado
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDate
import java.util.*

interface PollingDeputadoRepository : JpaRepository<PollingDeputado , Int> {
    fun findByDeputadoId(deputadoId : Long) : Optional<PollingDeputado>

    @Query("select a from PollingDeputado a where a.lastPull <= :lastPull order by a.lastPull asc")
    fun findAllWithLastPullBefore(@Param("lastPull") lastPull: LocalDate) : Set<PollingDeputado>
}