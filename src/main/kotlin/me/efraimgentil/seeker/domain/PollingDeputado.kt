package me.efraimgentil.seeker.domain

import java.time.LocalDate
import javax.persistence.*


@Entity
@Table(name = "polling_deputado")
data class PollingDeputado(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Int?,

    @Column(name = "deputado_id")
    var deputadoId : Long?,

    @Column(name = "last_pull")
    var lastPull : LocalDate?
)