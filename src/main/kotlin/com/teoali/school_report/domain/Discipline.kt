package com.teoali.school_report.domain

import jakarta.persistence.*

@Entity
@Table(name = "disciplines")
data class Discipline (

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    val name: String? = "",

    @Column
    val scoreFirst: Long? = 0,

    @Column
    val scoreSecond: Long? = 0,

    @Column
    val scoreThird: Long? = 0,

    @Column
    val scoreFourth: Long? = 0,

    @Column
    var score: Long? = 0,

    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false)
    val report: Report? = null
)