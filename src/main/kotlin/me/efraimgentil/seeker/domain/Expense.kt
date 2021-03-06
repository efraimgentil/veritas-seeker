package me.efraimgentil.seeker.domain

import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.OneToOne
import javax.persistence.PrePersist
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.SequenceGenerator
import javax.persistence.Table

@Entity
@Table(name = "expense")
data class Expense(
    @Id
    @SequenceGenerator(name="expense_id_seq" , sequenceName = "expense_id_seq" , initialValue = 1 , allocationSize = 500)
    @GeneratedValue(strategy = GenerationType.SEQUENCE , generator = "expense_id_seq")
    var id : Long? = null,
    @Column( name = "hash")
    var hash: String,
    @Column( name = "year")
    var year : Int,
    @Column( name = "month")
    var month : Int,
    @OneToOne(mappedBy = "expense", cascade = [CascadeType.PERSIST])
    @PrimaryKeyJoinColumn
    var document : ExpenseDocument? = null
){
    @PrePersist
    fun beforePersist(){
        document?.let { it.expense = this }
    }
}