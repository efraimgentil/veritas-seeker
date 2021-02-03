package me.efraimgentil.seeker.domain

import java.util.*
import javax.persistence.*

@Entity
@Table(name = "polling_expense")
data class PollingExpense(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id : Long? = null,
    @Column( name = "hash")
    var hash: String,
    @Column( name = "year")
    var year : Int,
    @Column( name = "month")
    var month : Int,
    @OneToMany(mappedBy = "pollingExpense", cascade = [CascadeType.PERSIST, CascadeType.MERGE])
    var documents : List<ExpenseDocument>? = null
)