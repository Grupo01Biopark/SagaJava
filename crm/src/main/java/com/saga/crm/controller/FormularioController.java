package com.saga.crm.controller;

import com.itextpdf.html2pdf.HtmlConverter;
import com.saga.crm.model.*;
import com.saga.crm.service.*;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.annotations.Check;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
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

    @Autowired
    private SpringTemplateEngine templateEngine;
    @Autowired
    private MailService mailService;

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

        List<Map<String, Object>> governancaChecklists = checklistService.getChecklistByEixo(3).stream()
                .map(checklist -> {
                    Map<String, Object> checklistMap = new HashMap<>();
                    checklistMap.put("id", checklist.getId());
                    checklistMap.put("titulo", checklist.getTitulo());
                    checklistMap.put("descricao", checklist.getDescricao());
                    return checklistMap;
                }).collect(Collectors.toList());

        List<Map<String, Object>> socialChecklists = checklistService.getChecklistByEixo(2).stream()
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
                map.put("ativo", true);
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

    @GetMapping("/formulario/respostas/{certId}")
    public Map<String, Object> verRespostas(@PathVariable("certId") Long certId) {
        Map<String, Object> respostaMap = new HashMap<>();
        Map<String, Object> formulario = new HashMap<>();
        List<Map<String, Object>> governancaPerguntas = new ArrayList<>();
        List<Map<String, Object>> ambientalPerguntas = new ArrayList<>();
        List<Map<String, Object>> socialPerguntas = new ArrayList<>();

        // Fetch responses based on certId
        List<Respostas> respostas = respostasService.findByCertificadoId(certId);

        // Log the retrieved respostas for debugging
        System.out.println("Respostas: " + respostas);

        // Assuming the first response is representative for formulario and empresaId
        if (!respostas.isEmpty()) {
            Respostas firstResposta = respostas.get(0);
            formulario.put("nome", firstResposta.getFormularioChecklists().getFormulario().getTitulo());
            formulario.put("id", firstResposta.getFormularioChecklists().getFormulario().getId());
            respostaMap.put("formulario", formulario);
            respostaMap.put("empresaId", firstResposta.getCertificado().getEmpresa().getId());
        }

        // Process each response to categorize questions
        for (Respostas resposta : respostas) {
            Map<String, Object> perguntaMap = new HashMap<>();
            perguntaMap.put("pergunta", resposta.getPergunta().getTitulo());
            perguntaMap.put("id", resposta.getPergunta().getId());
            perguntaMap.put("descricao", resposta.getPergunta().getDescricao());
            perguntaMap.put("resposta", resposta.getConformidade());
            perguntaMap.put("observacao", resposta.getObservacoes());

            // Log each perguntaMap for debugging
            System.out.println("Pergunta: " + perguntaMap);

            // Categorize based on eixo id
            if(resposta.getFormularioChecklists().getChecklist().getEixo().getId() == 1) {
                ambientalPerguntas.add(perguntaMap);
            } else if (resposta.getFormularioChecklists().getChecklist().getEixo().getId() == 2) {
                socialPerguntas.add(perguntaMap);
            } else if (resposta.getFormularioChecklists().getChecklist().getEixo().getId() == 3) {
                governancaPerguntas.add(perguntaMap);
            }
        }

        // Add categorized questions to the response map
        if (!governancaPerguntas.isEmpty()) {
            Map<String, Object> governanca = new HashMap<>();
            governanca.put("formularioChecklistId", respostas.get(0).getFormularioChecklists().getId());
            governanca.put("perguntas", governancaPerguntas);
            respostaMap.put("governanca", governanca);
        }

        if (!ambientalPerguntas.isEmpty()) {
            Map<String, Object> ambiental = new HashMap<>();
            ambiental.put("formularioChecklistId", respostas.get(0).getFormularioChecklists().getId());
            ambiental.put("perguntas", ambientalPerguntas);
            respostaMap.put("ambiental", ambiental);
        }

        if (!socialPerguntas.isEmpty()) {
            Map<String, Object> social = new HashMap<>();
            social.put("formularioChecklistId", respostas.get(0).getFormularioChecklists().getId());
            social.put("perguntas", socialPerguntas);
            respostaMap.put("social", social);
        }

        // Log the final response map for debugging
        System.out.println("Resposta Map: " + respostaMap);

        return respostaMap;
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
    public ResponseEntity<Map<String, Object>> salvarRespostas(
            @PathVariable("id") Long id,
            @PathVariable("empresaId") Long empresaId,
            @RequestBody Map<String, Object> requestBody) {

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

            // Criar e salvar o certificado
            Certificados certificados = new Certificados();
            LocalDateTime localDate = LocalDateTime.now();
            certificados.setData(localDate);
            certificados.setEmpresa(empresaService.getEmpresaById(empresaId));
            certificados.setFormulario(formularioService.getFormularioById(id));

            // Salvar o certificado primeiro
            certificadosService.save(certificados);

            // Obter o id do certificado salvo
            Long certificadoId = certificados.getId();
            certificados.setId(certificadoId);

            // Processar respostas e calcular notas
            boolean formularioReprovarGov = processarRespostas(respostasGov, formularioChecklistIdGov, certificados);
            boolean formularioReprovarAmb = processarRespostas(respostasAmb, formularioChecklistIdAmb, certificados);
            boolean formularioReprovarSoc = processarRespostas(respostasSoc, formularioChecklistIdSoc, certificados);

            // Definir notas
            certificados.setNota_gov(calcularNota(formularioReprovarGov, respostasGov));
            certificados.setNota_amb(calcularNota(formularioReprovarAmb, respostasAmb));
            certificados.setNota_soc(calcularNota(formularioReprovarSoc, respostasSoc));

            // Definir aprovação
            certificados.setAprovado(!formularioReprovarGov && !formularioReprovarAmb && !formularioReprovarSoc);

            // Atualizar o certificado com as notas e o status de aprovação
            certificadosService.save(certificados);

            boolean envioEmail;

            if(certificados.isAprovado()){
                 envioEmail = sendEmailCertificado(certificadoId);
            }else{
                envioEmail = false;
            }

            Map<String, Object> response = new HashMap<>();
            if(envioEmail){
                response.put("success", true);
                response.put("message", "Respostas salvas com sucesso");
            }else{
                response.put("success", true);
                response.put("message", "Respostas salvas com sucesso, email não enviado");
            }

            // Resposta JSON de sucesso


            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Resposta JSON de erro
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Erro ao salvar respostas");
            System.out.println(e.getMessage());
            errorResponse.put("error", e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }


    public Boolean sendEmailCertificado(Long id) throws IOException {
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

        try {
            String toEmail = certificado.getEmpresa().getEmail(); // Get the company's email
            String companyName = certificado.getEmpresa().getNomeFantasia(); // Get the company's name
            mailService.sendEmailWithCertificate(toEmail, companyName, target.toByteArray()); // Send the PDF as byte array
        } catch (Exception e) {
            // Handle the exception and return an error response if necessary
            return false;
        }

        return true;
    }

    private boolean processarRespostas(List<Map<String, Object>> respostas, Integer formularioChecklistId, Certificados certificado) {
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
            respostas1.setCertificado(certificado); // Setar o id do certificado nas respostas

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
