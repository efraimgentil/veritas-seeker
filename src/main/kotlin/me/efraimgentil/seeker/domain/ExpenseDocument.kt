package me.efraimgentil.seeker.domain

import javax.persistence.*

@Entity
@Table(name = "expense_document")
data class ExpenseDocument(
    @Id
    @Column(name = "polling_expense_id")
    var pollingExpenseId : Long? = null,
    @OneToOne
    @MapsId
    @JoinColumn(name="polling_expense_id")
    var pollingExpense : PollingExpense? = null,
    @Column( name = "body", columnDefinition = "json")
    var body : String
)