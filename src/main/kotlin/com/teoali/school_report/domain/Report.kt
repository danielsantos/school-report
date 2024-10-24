package com.teoali.school_report.domain

import jakarta.persistence.*

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
    val schoolName: String? = "",

    @OneToMany(mappedBy = "report", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val disciplines: List<Discipline> = emptyList()
)