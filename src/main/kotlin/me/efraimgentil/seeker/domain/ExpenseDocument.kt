package me.efraimgentil.seeker.domain

import javax.persistence.*

@Entity
@Table(name = "expense_document")
data class ExpenseDocument(
    @Id
    @Column( name = "hash")
    var hash: String,
    @Column(name = "polling_expense_id", updatable = false, insertable = false)
    var pollingExpenseId : Long? = null,
    @ManyToOne
    var pollingExpense : PollingExpense,
    //@Convert(converter = JsonConverter::class)
    @Column( name = "body", columnDefinition = "json")
    var body : String
)