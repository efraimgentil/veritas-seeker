package me.efraimgentil.seeker.domain

import javax.persistence.*

@Entity
@Table(name = "despesa")
data class Despesa(
    @Id
    @GeneratedValue
    var id : Long? = null,
    @Column( name = "documento_id")
    var documentId: Long,
    @Column( name = "ano")
    var ano : Int,
    @Column( name = "mes")
    var mes : Int
)