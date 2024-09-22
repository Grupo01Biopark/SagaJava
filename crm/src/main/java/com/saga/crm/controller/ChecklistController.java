package com.saga.crm.controller;

import com.saga.crm.model.*;
import com.saga.crm.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/checklists")
@CrossOrigin(origins = "*")
public class ChecklistController {

    private final PerguntasService perguntasService;
    private final ChecklistService checklistService;
    private final EixoService eixoService;
    private final SetorService setorService;
    private final PorteService porteService;
    private final ChecklistPerguntasService checklistPerguntasService;

    @Autowired
    public ChecklistController(ChecklistService checklistService, PerguntasService perguntasService, EixoService eixoService, SetorService setorService, PorteService porteService, ChecklistPerguntasService checklistPerguntasService) {
        this.perguntasService = perguntasService;
        this.checklistService = checklistService;
        this.eixoService = eixoService;
        this.setorService = setorService;
        this.porteService = porteService;
        this.checklistPerguntasService = checklistPerguntasService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getChecklists() {
        List<Checklist> checklists = checklistService.getActiveChecklists();
        List<Eixo> eixos = eixoService.getAllEixos();
        List<Setor> setores = setorService.getAllSetores();
        List<Porte> portes = porteService.getAllPortes();

        List<Map<String, Object>> checklistsFormat = checklists.stream()
                .map(checklist -> {
                    Map<String, Object> checklistMap = new HashMap<>();
                    checklistMap.put("id", checklist.getId());
                    checklistMap.put("titulo", checklist.getTitulo());
                    checklistMap.put("descricao", checklist.getDescricao());
                    checklistMap.put("eixo", checklist.getEixo());
                    checklistMap.put("setor", checklist.getSetor());
                    checklistMap.put("porte", checklist.getPorte());
                    checklistMap.put("quantidadePerguntas", checklist.getQuantidadePerguntas());
                    checklistMap.put("ativa", checklist.getStatus() == 1);
                    return checklistMap;
                }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("checklists", checklistsFormat);
        response.put("eixos", eixos);
        response.put("setores", setores);
        response.put("portes", portes);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/adicionar")
    public ResponseEntity<String> adicionarChecklist(@RequestBody Map<String, Object> checklistMap) {
        try {
            String titulo = (String) checklistMap.get("titulo");
            String descricao = (String) checklistMap.get("descricao");
            Long idEixo = ((Number) checklistMap.get("eixo")).longValue();
            Long idSetor = ((Number) checklistMap.get("setor")).longValue();
            Long idPorte = ((Number) checklistMap.get("porte")).longValue();
            List<Integer> idPerguntas = (List<Integer>) checklistMap.get("perguntas");

            Checklist checklist = new Checklist();
            checklist.setTitulo(titulo);
            checklist.setDescricao(descricao);
            checklist.setEixo(eixoService.getEixoById(idEixo));
            checklist.setSetor(setorService.getSetorById(idSetor));
            checklist.setPorte(porteService.getPorteById(idPorte));
            checklistService.save(checklist);

            for (Integer idPergunta : idPerguntas) {
                Perguntas pergunta = perguntasService.getPerguntaById(idPergunta.longValue());
                ChecklistPerguntas checklistPerguntas = new ChecklistPerguntas();
                checklistPerguntas.setChecklist(checklist);
                checklistPerguntas.setPerguntas(pergunta);
                checklistPerguntasService.save(checklistPerguntas);
            }

            return ResponseEntity.ok("Checklist criado com sucesso!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro ao criar o checklist: " + e.getMessage());
        }
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listarChecklists() {
        List<Checklist> checklists = checklistService.getActiveChecklists();
        List<Eixo> eixos = eixoService.getAllEixos();
        List<Setor> setores = setorService.getAllSetores();
        List<Porte> portes = porteService.getAllPortes();

        List<Map<String, Object>> checklistsFormat = checklists.stream()
                .map(checklist -> {
                    Map<String, Object> checklistMap = new HashMap<>();
                    checklistMap.put("id", checklist.getId());
                    checklistMap.put("titulo", checklist.getTitulo());
                    checklistMap.put("descricao", checklist.getDescricao());
                    checklistMap.put("eixo", checklist.getEixo());
                    checklistMap.put("setor", checklist.getSetor());
                    checklistMap.put("porte", checklist.getPorte());
                    checklistMap.put("quantidadePerguntas", checklist.getQuantidadePerguntas());
                    checklistMap.put("ativa", checklist.getStatus() == 1);
                    return checklistMap;
                }).collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("checklists", checklistsFormat);
        response.put("eixos", eixos);
        response.put("setores", setores);
        response.put("portes", portes);

        System.out.println(response);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/inativar/{id}")
    public ResponseEntity<Map<String, Object>> inativarChecklist(@PathVariable Long id) {
        Checklist checklist = checklistService.getChecklistById(id);

        if (checklist != null) {
            checklist.setStatus(2);
            checklistService.save(checklist);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Checklist inativado com sucesso");

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Checklist não encontrado");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }

    @GetMapping("/{id}/perguntas")
    public ResponseEntity<List<Object[]>> getPerguntasByChecklistId(@PathVariable Long id) {
        List<Object[]> perguntasArray = checklistPerguntasService.perguntasByChecklist(id);
        return ResponseEntity.ok(perguntasArray);
    }

    @GetMapping("/editar/{id}")
    public ResponseEntity<Checklist> editarChecklist(@PathVariable Long id) {
        Checklist checklist = checklistService.getChecklistById(id);

        if (checklist != null) {
            return ResponseEntity.ok(checklist);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<Map<String, Object>> editarChecklist(@PathVariable Long id, @RequestBody Checklist checklistAtualizado) {
        Checklist checklistExistente = checklistService.getChecklistById(id);

        if (checklistExistente != null) {
            checklistExistente.setTitulo(checklistAtualizado.getTitulo());
            checklistExistente.setDescricao(checklistAtualizado.getDescricao());

            checklistService.save(checklistExistente);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Checklist atualizado com sucesso");
            response.put("data", checklistExistente);

            return ResponseEntity.ok(response);
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Checklist não encontrado");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }
}
