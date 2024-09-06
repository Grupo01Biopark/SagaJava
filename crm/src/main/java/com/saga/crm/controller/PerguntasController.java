package com.saga.crm.controller;

import com.saga.crm.model.Eixo;
import com.saga.crm.model.Perguntas;
import com.saga.crm.model.Porte;
import com.saga.crm.model.Setor;
import com.saga.crm.service.EixoService;
import com.saga.crm.service.PerguntasService;
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
@RequestMapping("/perguntas")
public class PerguntasController {

    private final PerguntasService perguntasService;
    private final EixoService eixoService;
    private final SetorService setorService;
    private final PorteService porteService;

    @Autowired
    public PerguntasController(PerguntasService perguntasService, EixoService eixoService, SetorService setorService, PorteService porteService) {
        this.perguntasService = perguntasService;
        this.eixoService = eixoService;
        this.setorService = setorService;
        this.porteService = porteService;
    }

    @GetMapping("/listar")
    public ResponseEntity<Map<String, Object>> listarPerguntas() {
        List<Perguntas> perguntas = perguntasService.getAllPerguntas();
        List<Eixo> eixos = eixoService.getAllEixos();
        List<Setor> setores = setorService.getAllSetores();
        List<Porte> portes = porteService.getAllPortes();

        Map<String, Object> response = new HashMap<>();
        response.put("perguntas", perguntas);
        response.put("eixos", eixos);
        response.put("setores", setores);
        response.put("portes", portes);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/adicionar")
    public ResponseEntity<Map<String, Object>> adicionarPergunta(@RequestBody Perguntas perguntaRequest) {

        Perguntas pergunta = new Perguntas();
        pergunta.setDescricao(perguntaRequest.getDescricao());
        pergunta.setTitulo(perguntaRequest.getTitulo());

        // Processar Eixo
        Eixo eixo = eixoService.findEixoByTitulo(perguntaRequest.getEixo().getTitulo());
        if (eixo == null) {
            eixo = new Eixo();
            eixo.setTitulo(perguntaRequest.getEixo().getTitulo());
            eixoService.save(eixo);
        }
        pergunta.setEixo(eixo);

        // Processar Setor
        Setor setor = setorService.findSetorByTitulo(perguntaRequest.getSetor().getTitulo());
        if (setor == null) {
            setor = new Setor();
            setor.setTitulo(perguntaRequest.getSetor().getTitulo());
            setorService.save(setor);
        }
        pergunta.setSetor(setor);

        // Processar Porte
        Porte porte = porteService.findPorteByTitulo(perguntaRequest.getPorte().getTitulo());
        if (porte == null) {
            porte = new Porte();
            porte.setTitulo(perguntaRequest.getPorte().getTitulo());
            porteService.save(porte);
        }
        pergunta.setPorte(porte);

        try {
            perguntasService.save(pergunta);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Pergunta adicionada com sucesso");
            response.put("data", Map.of(
                    "id", pergunta.getId(),
                    "titulo", pergunta.getTitulo()
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }catch (Exception e) {

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao adicionar pergunta");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @PostMapping("/editar/{id}")
    public ResponseEntity<Map<String, Object>> atualizarPergunta(@PathVariable Long id, @RequestBody Perguntas pergunta) {
        Perguntas perguntaExistente = perguntasService.getPerguntaById(id);

        if (perguntaExistente != null) {
            // Validar e Processar Eixo
            Eixo eixo = eixoService.findEixoByTitulo(pergunta.getEixo().getTitulo());
            if (eixo == null) {
                eixo = new Eixo();
                eixo.setTitulo(pergunta.getEixo().getTitulo());
                eixoService.save(eixo);
            }
            perguntaExistente.setEixo(eixo);

            // Validar e Processar Setor
            Setor setor = setorService.findSetorByTitulo(pergunta.getSetor().getTitulo());
            if (setor == null) {
                setor = new Setor();
                setor.setTitulo(pergunta.getSetor().getTitulo());
                setorService.save(setor);
            }
            perguntaExistente.setSetor(setor);

            // Validar e Processar Porte
            Porte porte = porteService.findPorteByTitulo(pergunta.getPorte().getTitulo());
            if (porte == null) {
                porte = new Porte();
                porte.setTitulo(pergunta.getPorte().getTitulo());
                porteService.save(porte);
            }
            perguntaExistente.setPorte(porte);

            // Atualizar Título e Descrição
            perguntaExistente.setTitulo(pergunta.getTitulo());
            perguntaExistente.setDescricao(pergunta.getDescricao());

            try {
                perguntasService.save(perguntaExistente);

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Pergunta editada com sucesso");
                response.put("data", Map.of(
                        "id", perguntaExistente.getId(),
                        "titulo", perguntaExistente.getTitulo()
                ));

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }catch (Exception e) {

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Erro ao editar pergunta");
                errorResponse.put("error", e.getMessage());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        } else {
            Map<String, Object> notFoundResponse = new HashMap<>();
            notFoundResponse.put("success", false);
            notFoundResponse.put("message", "Pergunta não encontrada");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
        }
    }

    @DeleteMapping("/excluir/{id}")
    public ResponseEntity<Map<String, Object>> excluirPergunta(@PathVariable Long id) {
        Perguntas pergunta = perguntasService.getPerguntaById(id);

        if (pergunta != null) {
            try {
                perguntasService.excluirPergunta(pergunta.getId());

                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "Pergunta deletada com sucesso");
                response.put("data", Map.of(
                        "id", pergunta.getId(),
                        "titulo", pergunta.getTitulo()
                ));

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            }catch (Exception e) {

                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "Erro ao deletar pergunta");
                errorResponse.put("error", e.getMessage());

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
            }
        } else {
            Map<String, Object> notFoundResponse = new HashMap<>();
            notFoundResponse.put("success", false);
            notFoundResponse.put("message", "Pergunta não encontrada");

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(notFoundResponse);
        }
    }
}
