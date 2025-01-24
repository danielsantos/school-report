package com.teoali.school_report

import com.teoali.school_report.domain.Discipline
import com.teoali.school_report.domain.Report
import com.teoali.school_report.repository.DisciplineRepository
import com.teoali.school_report.repository.ReportRepository
import org.json.JSONObject
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model;
import java.awt.*
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.time.LocalDateTime
import javax.imageio.ImageIO
import java.util.Base64

@Controller
class HomeController(
    val reportRepository: ReportRepository,
    val disciplineRepository: DisciplineRepository
) {
    @Value("\${upload.directory}")
    lateinit var uploadDirectory: String

    @GetMapping("/")
    fun home(model: Model): String {
        model.addAttribute("report", Report(null, null))
        return "index_joy"
    }

    @GetMapping("/inicio")
    fun inicio(model: Model): String {
        return "inicio"
    }

    @GetMapping("/admin-scrt")
    fun adminScrt(model: Model): String {
        return reportRepository.findAll().toString()
    }

    @GetMapping("/boletim-escola")
    fun boletimEscola(model: Model): String {
        model.addAttribute("report", Report(null, null))
        return "index"
    }

    @PostMapping("/generate")
    fun generate(@ModelAttribute report: Report, model: Model): String {
        val reportSaved = reportRepository.save(report)
        model.addAttribute("report", report)
        model.addAttribute("discipline", Discipline(null, "", 0, 0, 0, 0, 0, reportSaved))
        return "view_report"
    }

    @PostMapping("/generate-joy")
    fun generateJoy(@ModelAttribute report: Report, @RequestParam("file") file: MultipartFile, model: Model): String {
        /****/
//        if (file.isEmpty) {
//            model.addAttribute("message", "Por favor, selecione um arquivo para upload!")
//            return "uploadForm"
//        }
//
//        if (!file.contentType?.startsWith("image/")!!) {
//            model.addAttribute("message", "O arquivo selecionado não é uma imagem.")
//            return "uploadForm"
//        }

        try {
            val uploadDir = File(uploadDirectory)
            if (!uploadDir.exists()) {
                uploadDir.mkdirs()
            }

            val filePath = File(uploadDir, file.originalFilename!!)
            file.transferTo(filePath)
        } catch (e: IOException) {
            model.addAttribute("message", "Erro ao enviar o arquivo: ${e.message}")
        }
        /****/

        val reportSaved = reportRepository.save(
            report.copy(imageName = file.originalFilename, dateCreated = LocalDateTime.now().toString())
        )

        listOf("Atividades Físicas", "Comportamento", "Socialização", "Obediência", "Alimentação").forEach { name ->
            disciplineRepository.save(Discipline(null, name, 0, 0, 0, 0, 0, reportSaved))
        }

        model.addAttribute("report", reportSaved)
        model.addAttribute("imagePath", uploadDirectory + "/" + file.originalFilename)
        model.addAttribute("discipline", Discipline(null, "", 0, 0, 0, 0, 0, reportSaved))
        model.addAttribute("disciplines", disciplineRepository.findByReportId(reportSaved.id!!))
        return "view_report_joy"
    }

    @PostMapping("/save_discipline")
    fun saveDiscipline(@ModelAttribute discipline: Discipline, model: Model): String {
        disciplineRepository.save(discipline)
        model.addAttribute("report", reportRepository.findById(discipline.report?.id!!).get())
        model.addAttribute("discipline", Discipline(null, "", 0, 0, 0, 0, 0, discipline.report))
        model.addAttribute("disciplines", disciplineRepository.findByReportId(discipline.report.id))
        return "view_report"
    }

    @PostMapping("/delete_discipline")
    fun deleteDiscipline(@ModelAttribute discipline: Discipline, model: Model): String {
        disciplineRepository.delete(discipline)
        model.addAttribute("report", reportRepository.findById(discipline.report?.id!!).get())
        model.addAttribute("discipline", Discipline(null, "", 0, 0, 0, 0, 0, discipline.report))
        model.addAttribute("disciplines", disciplineRepository.findByReportId(discipline.report.id))
        return "view_report_joy"
    }

    @PostMapping("/save_discipline_joy")
    fun saveDisciplineJoy(@ModelAttribute discipline: Discipline, model: Model): String {
        disciplineRepository.save(discipline)
        model.addAttribute("report", reportRepository.findById(discipline.report?.id!!).get())
        model.addAttribute("discipline", Discipline(null, "", 0, 0, 0, 0, 0, discipline.report))
        model.addAttribute("disciplines", disciplineRepository.findByReportId(discipline.report.id))
        return "view_report_joy"
    }

    @PostMapping("/finish")
    fun finish(@ModelAttribute report: Report, model: Model): String {
        model.addAttribute("report", reportRepository.findById(report.id!!).get())
        model.addAttribute("disciplines", disciplineRepository.findByReportId(report.id))
        return "finish"
    }

    @PostMapping("/html_code")
    fun getHtmlCode(@ModelAttribute codigoHTML: String, model: Model): String {
        println(codigoHTML)
        return "finish"
    }

    @PostMapping("/salvar-imagem")
    fun salvarImagem(@RequestBody imagem: Map<String, String>): ResponseEntity<String> {
        val base64Image = imagem["image"] ?: return ResponseEntity.badRequest().body("Imagem não recebida")
        val base64Data = base64Image.replace("data:image/png;base64,", "")

        try {
            val imageBytes = Base64.getDecoder().decode(base64Data)
            val path = Paths.get("captura.png")
            Files.write(path, imageBytes)

            return ResponseEntity.ok("Imagem salva com sucesso!")
        } catch (e: Exception) {
            e.printStackTrace()
            return ResponseEntity.status(500).body("Erro ao salvar a imagem")
        }
    }

    @PostMapping("/submitHtml")
    fun submitHtml(@RequestParam htmlContent: String): ResponseEntity<ByteArray> {
        val image = createImageFromHtml(htmlContent)

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", outputStream)
        val imageBytes = outputStream.toByteArray()

        val headers = HttpHeaders()
        headers.add("Content-Type", "image/png")

        return ResponseEntity(imageBytes, headers, HttpStatus.OK)
    }

    @PostMapping("/finish-joy")
    fun finishJoy(@ModelAttribute report: Report, model: Model): String {
        val jsonObject = JSONObject(report.mapAuxiliar)

        val hashMap = HashMap<String, Int>()
        jsonObject.keys().forEach {
            hashMap[it] = jsonObject.getInt(it)
        }

        val reportRetrieved = reportRepository.findById(report.id!!)

        model.addAttribute("report", reportRetrieved.get())
        val listDiscipline = disciplineRepository.findByReportId(report.id)

        model.addAttribute("imagePath", uploadDirectory + "/" + reportRetrieved.get().imageName)
        model.addAttribute("disciplines", generateNewList(hashMap, listDiscipline))
        return "finish_joy"
    }

    fun generateNewList(hashMap: HashMap<String, Int>, listDiscipline: List<Discipline>): List<Discipline> {
        val finalListDiscipline = mutableListOf<Discipline>()
        listDiscipline.map {
            finalListDiscipline.add(
                it.copy(score = hashMap[it.name]!!.toLong())
            )
        }
        return finalListDiscipline
    }

    @GetMapping("/tabela-imagem")
    fun getTableImage(): ResponseEntity<ByteArray> {
        val htmlTable = """
            <table>
                <tr>
                    <th>Nome</th>
                    <th>Idade</th>
                </tr>
                <tr>
                    <td>Fulano</td>
                    <td>30</td>
                </tr>
                <tr>
                    <td>Siclano</td>
                    <td>25</td>
                </tr>
            </table>
        """

        val image = createImageFromHtml(htmlTable)

        val outputStream = ByteArrayOutputStream()
        ImageIO.write(image, "png", outputStream)
        val imageBytes = outputStream.toByteArray()

        val headers = HttpHeaders()
        headers.add("Content-Type", "image/png")

        return ResponseEntity(imageBytes, headers, HttpStatus.OK)
    }

    private fun createImageFromHtml(html: String): BufferedImage {
        val document = Jsoup.parse(html)
        val table = document.select("table").first()

        val width = 500
        val height = 300
        val bufferedImage = BufferedImage(width, height, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.createGraphics()

        graphics.color = Color.WHITE
        graphics.fillRect(0, 0, width, height)
        graphics.color = Color.BLACK

        var y = 20
        for (row in table!!.select("tr")) {
            val cells = row.select("th, td")
            val rowText = cells.joinToString(" | ") { it.text() }
            graphics.drawString(rowText, 10, y)
            y += 20
        }

        graphics.dispose()
        return bufferedImage
    }
}
