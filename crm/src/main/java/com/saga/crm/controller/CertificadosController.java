package com.saga.crm.controller;

import com.saga.crm.model.Certificados;
import com.saga.crm.model.Checklist;
import com.saga.crm.service.CertificadosService;
import com.saga.crm.service.ChecklistService;
import com.saga.crm.service.EmpresaService;
import com.saga.crm.service.FormularioService;
import jakarta.servlet.http.HttpServletResponse;
import com.itextpdf.html2pdf.HtmlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/certificado")
@CrossOrigin(origins = "*")
public class CertificadosController {

    private final CertificadosService certificadosService;
    private final ChecklistService checklistService;
    private final EmpresaService empresaService;
    private final FormularioService formularioService;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Autowired
    public CertificadosController(CertificadosService certificadosService, ChecklistService checklistService, EmpresaService empresaService, FormularioService formularioService){
        this.certificadosService = certificadosService;
        this.checklistService = checklistService;
        this.empresaService = empresaService;
        this.formularioService = formularioService;
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> certificadosIndex(){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        List<Map<String, Object>> certificados = certificadosService.getAllCertificados().stream().map(
                certificado -> {
            Map<String, Object> response = new HashMap<>();
            response.put("id", certificado.getId());
            response.put("status", certificado.isAprovado());
            response.put("nota_gov", certificado.getNota_gov());
            response.put("nota_amb", certificado.getNota_amb());
            response.put("nota_soc", certificado.getNota_soc());
            response.put("date", certificado.getData().format(formatter));
            response.put("empresa", certificado.getEmpresa().getId());
            response.put("nomeEmpresa", certificado.getEmpresa().getNomeFantasia());
            response.put("formulario", certificado.getFormulario().getId());
            response.put("tituloFormulario", certificado.getFormulario().getTitulo());
            return response;
        }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("certificados", certificados);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/emitir")
    @ResponseBody
    public ResponseEntity<String> emitirCertificado(@PathVariable Long id, HttpServletResponse response) throws IOException {
        Certificados certificado = certificadosService.findById(id);

        Long formularioId = certificado.getFormulario().getId();

        List<Checklist> ambientalChecklist = checklistService.getChecklistByFormularioIdAndEixo(formularioId, 1);
        List<Checklist> governancaChecklist = checklistService.getChecklistByFormularioIdAndEixo(formularioId, 2);
        List<Checklist> socialChecklist = checklistService.getChecklistByFormularioIdAndEixo(formularioId, 3);


        Context context = new Context();
        context.setVariable("certificado", certificado);
        context.setVariable("ambientalChecklist", ambientalChecklist);
        context.setVariable("governancaChecklist", governancaChecklist);
        context.setVariable("socialChecklist", socialChecklist);

        String html = templateEngine.process("certificados/certificado", context);

        ByteArrayOutputStream target = new ByteArrayOutputStream();
        HtmlConverter.convertToPdf(new ByteArrayInputStream(html.getBytes()), target);

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=certificado.pdf");
        response.getOutputStream().write(target.toByteArray());

        return ResponseEntity.ok("teste");
    }
}
