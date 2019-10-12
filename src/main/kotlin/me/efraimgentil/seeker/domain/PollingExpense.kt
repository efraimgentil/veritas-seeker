package me.efraimgentil.seeker.domain

import javax.persistence.*

@Entity
@Table(name = "polling_expense")
data class PollingExpense(
    @Id
    @GeneratedValue
    var id : Long? = null,
    @Column( name = "document_id")
    var documentId: Long,
    @Column( name = "yeah")
    var year : Int,
    @Column( name = "month")
    var month : Int
)