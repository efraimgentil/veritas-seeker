package me.efraimgentil.seeker.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.MapsId
import javax.persistence.OneToOne
import javax.persistence.Table

@Entity
@Table(name = "expense_document")
data class ExpenseDocument(
        @Id
    @Column(name = "expense_id")
    var expenseId : Long? = null,
        @OneToOne(fetch = FetchType.LAZY)
    @MapsId
    @JoinColumn(name="expense_id")
    var expense : Expense? = null,
        @Column( name = "body", columnDefinition = "json")
    var body : String
){
    override fun toString(): String {
        return "{ \"expenseId\" : ${expenseId}, \"body\": ${body} }"
    }
}