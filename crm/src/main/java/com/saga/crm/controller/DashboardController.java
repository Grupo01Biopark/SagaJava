package com.saga.crm.controller;

import com.saga.crm.model.Checklist;
import com.saga.crm.model.Empresa;
import com.saga.crm.service.ChecklistService;
import com.saga.crm.service.EmpresaService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {
    private final ChecklistService checklistService;
    private final EmpresaService empresaService;

    @Autowired
    public DashboardController(
            ChecklistService checklistService,
            EmpresaService empresaService
    ) {
        this.checklistService = checklistService;
        this.empresaService = empresaService;
    }

    @GetMapping("/checklist")
    public ResponseEntity<Integer> getChecklist(Model model) {
        List<Checklist> checklists = checklistService.getAllChecklists();
        Integer contagemChecklists = 0;
        for (Checklist checklist : checklists) {
            contagemChecklists++;
        }
        return ResponseEntity.ok(contagemChecklists);
    }

    @GetMapping("/porte")
    public ResponseEntity<Map<String, Long>> getEmpresasPorPorte() {
        Map<String, Long> empresasPorPorte = empresaService.getEmpresasPorPorte();
        return ResponseEntity.ok(empresasPorPorte);
    }
    @GetMapping("/setor")
    public ResponseEntity<Map<String, Long>> getEmpresasPorSetor() {
        Map<String, Long> empresasPorSetor = empresaService.getEmpresasPorSetor();
        return ResponseEntity.ok(empresasPorSetor);
    }
    @GetMapping("/total")
    public ResponseEntity<Long> getTotalEmpresas() {
        long totalEmpresas = empresaService.getAllEmpresas().size();
        return ResponseEntity.ok(totalEmpresas);
    }
    @GetMapping("/mes")
    public ResponseEntity<List<Object[]>> getParecerEmpresasPorMes() {
        List<Object[]> parecer = empresaService.countEmpresasByDataCadastro();
        return ResponseEntity.ok(parecer);
    }
}