package com.teoali.school_report.domain

import jakarta.persistence.*
import kotlin.jvm.Transient

@Entity
@Table(name = "reports")
data class Report (

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    val name: String? = "",

    @Column
    val serie: String? = "",

    @Column
    val nickname: String? = "",

    @Column
    val schoolName: String? = "",

    @Column
    val imageName: String? = "",

    @Column
    val dateCreated: String? = "",

    @Transient
    val mapAuxiliar: String? = "" ,

    @OneToMany(mappedBy = "report", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val disciplines: List<Discipline> = emptyList()
)