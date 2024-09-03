package com.saga.crm.controller;

import com.saga.crm.model.Empresa;
import com.saga.crm.model.Porte;
import com.saga.crm.model.Setor;
import com.saga.crm.service.EmpresaService;
import com.saga.crm.service.PorteService;
import com.saga.crm.service.SetorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/empresas")
@CrossOrigin(origins = "*")
public class EmpresaController {

    private final EmpresaService empresaService;
    private final PorteService porteService;
    private final SetorService setorService;

    @Autowired
    public EmpresaController(EmpresaService empresaService, PorteService porteService, SetorService setorService) {
        this.empresaService = empresaService;
        this.porteService = porteService;
        this.setorService = setorService;
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> getEmpresasData() {
        List<Empresa> empresas = empresaService.getAllEmpresas();
        List<Porte> portes = porteService.getAllPortes();
        List<Setor> setores = setorService.getAllSetores();

        Map<String, Object> response = new HashMap<>();
        response.put("empresas", empresas);
        response.put("portes", portes);
        response.put("setores", setores);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/adicionar")
    public ResponseEntity<?> adicionarEmpresa(@RequestBody Empresa empresa) {
        try {
            empresaService.cadastrarEmpresa(empresa);
            return ResponseEntity.ok(empresa);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao adicionar empresa: " + e.getMessage());
        }
    }

    @GetMapping("/verificarCnpj")
    public ResponseEntity<?> verificarCnpj(@RequestParam String cnpj) {
        try {
            boolean cnpjCadastrado = empresaService.cnpjJaCadastrado(cnpj);
            return ResponseEntity.ok(cnpjCadastrado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erro ao verificar CNPJ: " + e.getMessage());
        }
    }

    @PostMapping("/editar/{id}")
    public ResponseEntity<?> editarEmpresa(@PathVariable Long id, @RequestBody Empresa empresa) {
        try {
            empresa.setId(id);
            empresaService.editarEmpresa(empresa);
            return ResponseEntity.ok(empresa);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao editar empresa: " + e.getMessage());
        }
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<?> excluirEmpresa(@PathVariable Long id) {
        try {
            empresaService.excluirEmpresa(id);
            return ResponseEntity.ok("Empresa excluída com sucesso.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Falha ao excluir empresa: " + e.getMessage());
        }
    }
}
