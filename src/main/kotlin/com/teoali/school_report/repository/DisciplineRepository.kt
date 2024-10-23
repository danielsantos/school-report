package com.teoali.school_report.repository

import com.teoali.school_report.domain.Discipline
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface DisciplineRepository : JpaRepository<Discipline, Long> {
    fun findByReportId(reportId: Long): List<Discipline>
}