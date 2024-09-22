package com.saga.crm.controller;

import com.saga.crm.model.*;
import com.saga.crm.service.*;
import org.hibernate.annotations.Check;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class FormularioController {
    private final FormularioService formularioService;
    private final ChecklistService checklistService;
    private final EmpresaService empresaService;
    private final PerguntasService perguntasService;
    private final RespostasService respostasService;

    private final CertificadosService certificadosService;
    private final FormularioChecklistService formularioChecklistService;

    public FormularioController(FormularioService formularioService, ChecklistService checklistService, FormularioChecklistService formularioChecklistService, EmpresaService empresaService, PerguntasService perguntasService, RespostasService respostasService, CertificadosService certificadosService) {
        this.formularioService = formularioService;
        this.checklistService = checklistService;
        this.formularioChecklistService = formularioChecklistService;
        this.empresaService = empresaService;
        this.perguntasService = perguntasService;
        this.respostasService = respostasService;
        this.certificadosService = certificadosService;

    }

    @GetMapping("/formulario")
    public ResponseEntity<Map<String, Object>> formularioForm() {
        // Obter as listas de checklists por eixo
        List<Map<String, Object>> ambientalChecklists = checklistService.getChecklistByEixo(1).stream()
                .map(checklist -> {
                    Map<String, Object> checklistMap = new HashMap<>();
                    checklistMap.put("id", checklist.getId());
                    checklistMap.put("titulo", checklist.getTitulo());
                    checklistMap.put("descricao", checklist.getDescricao());
                    return checklistMap;
                }).collect(Collectors.toList());

        List<Map<String, Object>> governancaChecklists = checklistService.getChecklistByEixo(2).stream()
                .map(checklist -> {
                    Map<String, Object> checklistMap = new HashMap<>();
                    checklistMap.put("id", checklist.getId());
                    checklistMap.put("titulo", checklist.getTitulo());
                    checklistMap.put("descricao", checklist.getDescricao());
                    return checklistMap;
                }).collect(Collectors.toList());

        List<Map<String, Object>> socialChecklists = checklistService.getChecklistByEixo(3).stream()
                .map(checklist -> {
                    Map<String, Object> checklistMap = new HashMap<>();
                    checklistMap.put("id", checklist.getId());
                    checklistMap.put("titulo", checklist.getTitulo());
                    checklistMap.put("descricao", checklist.getDescricao());
                    return checklistMap;
                }).collect(Collectors.toList());

        // Montar a resposta JSON
        Map<String, Object> response = new HashMap<>();
        response.put("ambientalChecklists", ambientalChecklists);
        response.put("governancaChecklists", governancaChecklists);
        response.put("socialChecklists", socialChecklists);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/formulario/adicionar")
    public ResponseEntity<Map<String, Object>> adicionarFormulario(@RequestBody Map<String, Object> requestData) {
        try {
            // Extrair os campos do corpo da requisição
            String titulo = (String) requestData.get("titulo");
            String descricao = (String) requestData.get("descricao");
            Long governancaChecklistId = ((Number) requestData.get("governancaChecklist")).longValue();
            Long ambientalChecklistId = ((Number) requestData.get("ambientalChecklist")).longValue();
            Long socialChecklistId = ((Number) requestData.get("socialChecklist")).longValue();

            // Criar e salvar o formulário
            Formulario formulario = new Formulario();
            formulario.setTitulo(titulo);
            formulario.setDescricao(descricao);
            formularioService.save(formulario);

            // Salvar o Formulario de Governança
            FormularioChecklist formularioChecklistGovernanca = new FormularioChecklist();
            formularioChecklistGovernanca.setFormulario(formulario);
            formularioChecklistGovernanca.setChecklist(checklistService.getChecklistById(governancaChecklistId));
            formularioChecklistService.save(formularioChecklistGovernanca);

            // Salvar o Formulario de Ambiental
            FormularioChecklist formularioChecklistAmbiental = new FormularioChecklist();
            formularioChecklistAmbiental.setFormulario(formulario);
            formularioChecklistAmbiental.setChecklist(checklistService.getChecklistById(ambientalChecklistId));
            formularioChecklistService.save(formularioChecklistAmbiental);

            // Salvar o Formulario de Social
            FormularioChecklist formularioChecklistSocial = new FormularioChecklist();
            formularioChecklistSocial.setFormulario(formulario);
            formularioChecklistSocial.setChecklist(checklistService.getChecklistById(socialChecklistId));
            formularioChecklistService.save(formularioChecklistSocial);

            // Resposta JSON com sucesso
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Formulário criado com sucesso");
            response.put("data", Map.of(
                    "id", formulario.getId(),
                    "titulo", formulario.getTitulo(),
                    "descricao", formulario.getDescricao()
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (Exception e) {
            // Resposta JSON com erro
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao criar o formulário");
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    @GetMapping("/formulario/listar")
    public List<Map<String, Object>> listarFormularios() {
    return formularioService.getAllFormulario().stream()
            .map(formulario -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", formulario.getId());
                map.put("titulo", formulario.getTitulo());
                map.put("descricao", formulario.getDescricao());
                map.put("checklists", formulario.getFormularioChecklists().stream()
                        .findFirst()
                        .map(formularioChecklist -> {
                            Map<String, Object> checklistMap = new HashMap<>();
                            checklistMap.put("setor", formularioChecklist.getChecklist().getSetor().getTitulo());
                            checklistMap.put("porte", formularioChecklist.getChecklist().getPorte().getTitulo());
                            return checklistMap;
                        })
                        .orElse(null));
                return map;
            })
            .collect(Collectors.toList());
}

    @GetMapping("/formulario/listar/empresas/{id}")
    public Map<String, Object> iniciarFormulario(@PathVariable("id") Long id) {
        Formulario formulario = formularioService.getFormularioById(id);

        if (formulario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Formulário não encontrado");
        }

        List<Map<String, Object>> empresas = empresaService.getAllEmpresas().stream()
                .map(empresa -> {
                    Map<String, Object> empresaMap = new HashMap<>();
                    empresaMap.put("id", empresa.getId());
                    empresaMap.put("nome", empresa.getNomeFantasia());
                    empresaMap.put("cnpj", formatCNPJ(empresa.getCnpj()));
                    return empresaMap;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("empresas", empresas);

        return response;
    }



    @GetMapping("/formulario/{id}/iniciar/respostas/{empresaId}")
    public Map<String, Object> iniciarFormularioRespostas(
            @PathVariable("id") Long id,
            @PathVariable("empresaId") Long empresaId) {

        Formulario formulario = formularioService.getFormularioById(id);

        if (formulario == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Formulário não encontrado");
        }

        // Criação da resposta
        Map<String, Object> response = new HashMap<>();

        // Governança
        List<Checklist> governancaChecklistList = checklistService.getChecklistByFormularioIdAndEixo(id, 2);
        Checklist governancaChecklist = governancaChecklistList.isEmpty() ? null : governancaChecklistList.get(0);

        if (governancaChecklist != null) {
            Long governancaChecklistId = governancaChecklist.getId();
            List<Perguntas> governancaPerguntas = checklistService.getChecklistPerguntasById(governancaChecklistId);
            FormularioChecklist formularioChecklistGovernanca = formularioChecklistService.findByFormularioAndChecklist(id, governancaChecklistId);

            // Criação do mapa de Governança
            Map<String, Object> governancaMap = new HashMap<>();
            governancaMap.put("formularioChecklistId", formularioChecklistGovernanca.getId());
            governancaMap.put("perguntas", governancaPerguntas.stream()
                    .map(pergunta -> {
                        Map<String, Object> perguntaMap = new HashMap<>();
                        perguntaMap.put("id", pergunta.getId());
                        perguntaMap.put("pergunta", pergunta.getTitulo());
                        perguntaMap.put("descricao", pergunta.getDescricao());
                        return perguntaMap;
                    })
                    .collect(Collectors.toList())
            );
            response.put("governanca", governancaMap);
        }

        // Ambiental
        List<Checklist> ambientalChecklistList = checklistService.getChecklistByFormularioIdAndEixo(id, 1);
        Checklist ambientalChecklist = ambientalChecklistList.isEmpty() ? null : ambientalChecklistList.get(0);

        if (ambientalChecklist != null) {
            Long ambientalChecklistId = ambientalChecklist.getId();
            List<Perguntas> ambientalPerguntas = checklistService.getChecklistPerguntasById(ambientalChecklistId);
            FormularioChecklist formularioChecklistAmbiental = formularioChecklistService.findByFormularioAndChecklist(id, ambientalChecklistId);

            // Criação do mapa de Ambiental
            Map<String, Object> ambientalMap = new HashMap<>();
            ambientalMap.put("formularioChecklistId", formularioChecklistAmbiental.getId());
            ambientalMap.put("perguntas", ambientalPerguntas.stream()
                    .map(pergunta -> {
                        Map<String, Object> perguntaMap = new HashMap<>();
                        perguntaMap.put("id", pergunta.getId());
                        perguntaMap.put("pergunta", pergunta.getTitulo());
                        perguntaMap.put("descricao", pergunta.getDescricao());
                        return perguntaMap;
                    })
                    .collect(Collectors.toList())
            );
            response.put("ambiental", ambientalMap);
        }

        // Social
        List<Checklist> socialChecklistList = checklistService.getChecklistByFormularioIdAndEixo(id, 3);
        Checklist socialChecklist = socialChecklistList.isEmpty() ? null : socialChecklistList.get(0);

        if (socialChecklist != null) {
            Long socialChecklistId = socialChecklist.getId();
            List<Perguntas> socialPerguntas = checklistService.getChecklistPerguntasById(socialChecklistId);
            FormularioChecklist formularioChecklistSocial = formularioChecklistService.findByFormularioAndChecklist(id, socialChecklistId);

            // Criação do mapa de Social
            Map<String, Object> socialMap = new HashMap<>();
            socialMap.put("formularioChecklistId", formularioChecklistSocial.getId());
            socialMap.put("perguntas", socialPerguntas.stream()
                    .map(pergunta -> {
                        Map<String, Object> perguntaMap = new HashMap<>();
                        perguntaMap.put("id", pergunta.getId());
                        perguntaMap.put("pergunta", pergunta.getTitulo());
                        perguntaMap.put("descricao", pergunta.getDescricao());
                        return perguntaMap;
                    })
                    .collect(Collectors.toList())
            );
            response.put("social", socialMap);
        }

        // Adicionando informações da empresa e do formulário à resposta
        response.put("empresaId", empresaId);
        response.put("formulario", Map.of(
                "id", formulario.getId(),
                "nome", formulario.getTitulo()
        ));

        return response;
    }

    @PostMapping("/formulario/{id}/iniciar/respostas/{empresaId}/salvar")
    public ResponseEntity<Map<String, Object>> salvarRespostas(@PathVariable("id") Long id, @PathVariable("empresaId") Long empresaId, @RequestBody Map<String, Object> requestBody) {

        try {
            List<Map<String, Object>> respostas = (List<Map<String, Object>>) requestBody.get("respostas");
            System.out.println(respostas);
            // Extrair e processar respostas
            Map<String, Object> respostaGovObj = respostas.get(0);
            List<Map<String, Object>> respostasGov = (List<Map<String, Object>>) respostaGovObj.get("respostasGov");
            Integer formularioChecklistIdGov = Integer.parseInt((String) respostaGovObj.get("idFormularioChecklistGov"));

            Map<String, Object> respostaAmbObj = respostas.get(1);
            List<Map<String, Object>> respostasAmb = (List<Map<String, Object>>) respostaAmbObj.get("respostasAmb");
            Integer formularioChecklistIdAmb = Integer.parseInt((String) respostaAmbObj.get("idFormularioChecklistAmb"));

            Map<String, Object> respostaSocObj = respostas.get(2);
            List<Map<String, Object>> respostasSoc = (List<Map<String, Object>>) respostaSocObj.get("respostasSoc");
            Integer formularioChecklistIdSoc = Integer.parseInt((String) respostaSocObj.get("idFormularioChecklistSoc"));

            Certificados certificados = new Certificados();
            LocalDateTime localDate = LocalDateTime.now();
            certificados.setData(localDate);
            certificados.setEmpresa(empresaService.getEmpresaById(empresaId));

            // Processar respostas e calcular notas
            boolean formularioReprovarGov = processarRespostas(respostasGov, formularioChecklistIdGov);
            boolean formularioReprovarAmb = processarRespostas(respostasAmb, formularioChecklistIdAmb);
            boolean formularioReprovarSoc = processarRespostas(respostasSoc, formularioChecklistIdSoc);

            // Definir notas
            certificados.setNota_gov(calcularNota(formularioReprovarGov, respostasGov));
            certificados.setNota_amb(calcularNota(formularioReprovarAmb, respostasAmb));
            certificados.setNota_soc(calcularNota(formularioReprovarSoc, respostasSoc));

            // Definir aprovação
            certificados.setAprovado(!formularioReprovarGov && !formularioReprovarAmb && !formularioReprovarSoc);

            certificados.setFormulario(formularioService.getFormularioById(id));
            certificadosService.save(certificados);

            // Resposta JSON de sucesso
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Respostas salvas com sucesso");

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Resposta JSON de erro
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao salvar respostas");
            System.out.println(e.getMessage());
//            System.out.println(e.getMessage());
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private boolean processarRespostas(List<Map<String, Object>> respostas, Integer formularioChecklistId) {
        boolean formularioReprovar = false;
        int existeMedio = 0;

        for (Map<String, Object> resposta : respostas) {
            FormularioChecklist formularioChecklist = formularioChecklistService.getFormularioChecklistById(Long.valueOf(formularioChecklistId));

            String idPergunta = (String) resposta.get("idPergunta");
            Perguntas pergunta = perguntasService.getPerguntaById(Long.valueOf(idPergunta));

            Integer conformidade = Integer.parseInt((String) resposta.get("conformidade"));
            String observacoes = (String) resposta.get("observacoes");

            Respostas respostas1 = new Respostas();
            respostas1.setConformidade(conformidade);
            respostas1.setPergunta(pergunta);
            respostas1.setFormularioChecklists(formularioChecklist);
            respostas1.setObservacoes(observacoes);
            respostasService.save(respostas1);

            if (conformidade == 3) {
                formularioReprovar = true;
            } else if (conformidade == 2) {
                existeMedio++;
            }
        }

        return formularioReprovar;
    }

    private Long calcularNota(boolean formularioReprovar, List<Map<String, Object>> respostas) {
        if (formularioReprovar) {
            return 3L;
        } else {
            long existeMedio = respostas.stream()
                    .filter(resposta -> Integer.parseInt((String) resposta.get("conformidade")) == 2)
                    .count();
            return (existeMedio > 1) ? 2L : 1L;
        }
    }

    public static String formatCNPJ(String cnpj) {
        return cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d+)", "$1.$2.$3/$4-$5");
    }

}
