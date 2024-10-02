package com.teoali.school_report

import com.teoali.school_report.domain.Report
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute

@Controller
class HomeController {

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("report", Report(null, null))
        return "index"
    }

    @PostMapping("/generate")
    fun generate(@ModelAttribute report: Report, model: Model): String {
        model.addAttribute("report", report)
        return "view_report"
    }
}
