package me.efraimgentil.seeker.domain

import java.time.LocalDate
import javax.persistence.*


@Entity
@Table(name = "polling_congressman")
data class PollingCongressman(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id: Int?,

        @Column(name = "congressman_id")
        var congressmanId: Long?,

        @Column(name = "last_pull")
        var lastPull: LocalDate?
)