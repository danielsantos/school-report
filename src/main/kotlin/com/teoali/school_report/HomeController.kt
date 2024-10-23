package com.teoali.school_report

import com.teoali.school_report.domain.Discipline
import com.teoali.school_report.domain.Report
import com.teoali.school_report.repository.DisciplineRepository
import com.teoali.school_report.repository.ReportRepository
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute

@Controller
class HomeController(
    val reportRepository: ReportRepository,
    val disciplineRepository: DisciplineRepository
) {

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("report", Report(null, null))
        return "index"
    }

    @PostMapping("/generate")
    fun generate(@ModelAttribute report: Report, model: Model): String {
        val reportSaved = reportRepository.save(report)
        model.addAttribute("report", report)
        model.addAttribute("discipline", Discipline(null, "", 0, 0, 0, 0, reportSaved))
        return "view_report"
    }

    @PostMapping("/save_discipline")
    fun saveDiscipline(@ModelAttribute discipline: Discipline, model: Model): String {
        disciplineRepository.save(discipline)
        model.addAttribute("report", reportRepository.findById(discipline.report?.id!!).get())
        model.addAttribute("discipline", Discipline(null, "", 0, 0, 0, 0, discipline.report))
        model.addAttribute("disciplines", disciplineRepository.findByReportId(discipline.report.id))
        return "view_report"
    }

    @PostMapping("/finish")
    fun finish(@ModelAttribute report: Report, model: Model): String {
        model.addAttribute("report", reportRepository.findById(report.id!!).get())
        model.addAttribute("disciplines", disciplineRepository.findByReportId(report.id))
        return "finish"
    }
}
