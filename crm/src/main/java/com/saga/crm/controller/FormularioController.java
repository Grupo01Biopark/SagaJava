package com.saga.crm.controller;

import com.saga.crm.model.*;
import com.saga.crm.service.*;
import org.hibernate.annotations.Check;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
    public String formularioForm(Model model) {
        List<Checklist> ambientalChecklists = checklistService.getChecklistByEixo(1);
        List<Checklist> governancaChecklists = checklistService.getChecklistByEixo(2);
        List<Checklist> socialChecklists = checklistService.getChecklistByEixo(3);

        model.addAttribute("ambientalChecklists", ambientalChecklists);
        model.addAttribute("governancaChecklists", governancaChecklists);
        model.addAttribute("socialChecklists", socialChecklists);

        return "formularios/index";
    }

    @PostMapping("/formulario/adicionar")
    public String adicionarFormulario(@RequestParam("titulo") String titulo, @RequestParam("descricao") String descricao, @RequestParam("governancaChecklist") Long governancaChecklistId, @RequestParam("ambientalChecklist") Long ambientalChecklistId, @RequestParam("socialChecklist") Long socialChecklistId)  {
        Formulario formulario = new Formulario();
        formulario.setTitulo(titulo);
        formulario.setDescricao(descricao);
        formularioService.save(formulario);

        //Salvar o Formulario de Governança
        FormularioChecklist formularioChecklistGovernanca = new FormularioChecklist();
        formularioChecklistGovernanca.setFormulario(formulario);
        formularioChecklistGovernanca.setChecklist(checklistService.getChecklistById(governancaChecklistId));
        formularioChecklistService.save(formularioChecklistGovernanca);

        //Salvar o Formulario de Ambiental
        FormularioChecklist formularioChecklistAmbiental = new FormularioChecklist();
        formularioChecklistAmbiental.setFormulario(formulario);
        formularioChecklistAmbiental.setChecklist(checklistService.getChecklistById(ambientalChecklistId));
        formularioChecklistService.save(formularioChecklistAmbiental);

        //Salvar o Formulario de Social
        FormularioChecklist formularioChecklistSocial = new FormularioChecklist();
        formularioChecklistSocial.setFormulario(formulario);
        formularioChecklistSocial.setChecklist(checklistService.getChecklistById(socialChecklistId));
        formularioChecklistService.save(formularioChecklistSocial);


        return "redirect:/formulario";
    }

    @GetMapping("/formulario/listar")
    public List<Formulario> listarFormularios() {
        return formularioService.getAllFormulario();
    }

    @GetMapping("/formulario/{id}/iniciar")
    public String iniciarFormulario(@PathVariable("id") Long id, Model model) {
        Formulario formulario = formularioService.getFormularioById(id);

        List<Empresa> empresas = empresaService.getAllEmpresas();

        // Formatar o CNPJ de cada empresa
        for (Empresa empresa : empresas) {
            empresa.setCnpj(formatCNPJ(empresa.getCnpj()));
        }


        model.addAttribute("formulario", formulario);
        model.addAttribute("empresas", empresas);

        return "formularios/iniciar";
    }
    @GetMapping("/formulario/{id}/iniciar/respostas/{empresaId}")
    public String iniciarFormularioRespostas(@PathVariable("id") Long id, Model model, @PathVariable("empresaId") Long empresaId) {
        Formulario formulario = formularioService.getFormularioById(id);

//        Perguntas Governança
        List<Checklist> governancaChecklistList = checklistService.getChecklistByFormularioIdAndEixo(id, 2);

        Checklist governancaChecklist = null;
        if (!governancaChecklistList.isEmpty()) {
            governancaChecklist = governancaChecklistList.get(0);
        }
        if (governancaChecklist != null) {
            Long governancaChecklistId = governancaChecklist.getId();
            List<Perguntas> governancaPerguntas = checklistService.getChecklistPerguntasById(governancaChecklistId);

            FormularioChecklist formularioChecklistGovernanca = formularioChecklistService.findByFormularioAndChecklist(id, governancaChecklistId);

            System.out.println("FormularioChecklist: " + formularioChecklistGovernanca.getId());

            model.addAttribute("formularioChecklistGovernanca", formularioChecklistGovernanca.getId());
            model.addAttribute("govPerguntas", governancaPerguntas);
        }


//        Perguntas Ambiental
        List<Checklist> ambientalChecklistList = checklistService.getChecklistByFormularioIdAndEixo(id, 1);


        Checklist ambientalChecklist = null;
        if (!ambientalChecklistList.isEmpty()) {
            ambientalChecklist = ambientalChecklistList.get(0);
        }
        if (ambientalChecklist != null) {
            Long ambientalChecklistId = ambientalChecklist.getId();
            List<Perguntas> ambientalPerguntas = checklistService.getChecklistPerguntasById(ambientalChecklistId);

            FormularioChecklist formularioChecklistAmbiental= formularioChecklistService.findByFormularioAndChecklist(id, ambientalChecklistId);

            System.out.println("FormularioChecklist: " + formularioChecklistAmbiental.getId());

            model.addAttribute("formularioChecklistAmbiental", formularioChecklistAmbiental.getId());
            model.addAttribute("ambPerguntas", ambientalPerguntas);
        }

//        Perguntas Social
        List<Checklist> socialChecklistList = checklistService.getChecklistByFormularioIdAndEixo(id, 3);

        Checklist socialChecklist = null;
        if (!socialChecklistList.isEmpty()) {
            socialChecklist = socialChecklistList.get(0);
        }
        if (socialChecklist != null) {
            Long socialChecklistId = socialChecklist.getId();
            List<Perguntas> socialPerguntas = checklistService.getChecklistPerguntasById(socialChecklistId);

            FormularioChecklist formularioChecklistSocial = formularioChecklistService.findByFormularioAndChecklist(id, socialChecklistId);

            System.out.println("FormularioChecklist: " + formularioChecklistSocial.getId());

            model.addAttribute("formularioChecklistSocial", formularioChecklistSocial.getId());
            model.addAttribute("socPerguntas", socialPerguntas);
        }

        model.addAttribute("empresaId", empresaId);
        model.addAttribute("formulario", formulario);



        return "formularios/respostas";
    }

    @PostMapping("/formulario/{id}/iniciar/respostas/{empresaId}/salvar")
    public String salvarRespostas(@PathVariable("id") Long id, @PathVariable("empresaId") Long empresaId, @RequestBody Map<String, Object> requestBody) {


        List<Map<String, Object>> respostas = (List<Map<String, Object>>) requestBody.get("respostas");
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
        certificados.setData(new Date());
        certificados.setEmpresa(empresaService.getEmpresaById(empresaId));

        boolean formularioReprovarGov = false;
        Integer existeMedioGov = 0;
        for (Map<String, Object> resposta : respostasGov) {
            FormularioChecklist formularioChecklistgov = formularioChecklistService.getFormularioChecklistById(Long.valueOf(formularioChecklistIdGov));

            String idPergunta = (String) resposta.get("idPergunta");
            Perguntas pergunta = perguntasService.getPerguntaById(Long.valueOf(idPergunta));

            Integer conformidade = Integer.parseInt((String) resposta.get("conformidade"));
            String observacoes = (String) resposta.get("observacoes");

            Respostas respostas1 = new Respostas();
            respostas1.setConformidade(conformidade);
            respostas1.setPergunta(pergunta);
            respostas1.setFormularioChecklists(formularioChecklistgov);
            respostas1.setObservacoes(observacoes);
            respostasService.save(respostas1);

            if(conformidade == 3){
                formularioReprovarGov = true;
            }else{
                if(conformidade == 2){
                    existeMedioGov++;
                }
            }
        }

        //        Nota Governança
        if(formularioReprovarGov){
            certificados.setNota_gov(3L);
        }else{
            if (existeMedioGov > 1){
                certificados.setNota_gov(2L);
            }else{
                certificados.setNota_gov(1L);
            }
        }

        boolean formularioReprovarAmb = false;
        Integer existeMedioAmb = 0;
        for (Map<String, Object> resposta : respostasAmb) {
            FormularioChecklist formularioChecklistAmb = formularioChecklistService.getFormularioChecklistById(Long.valueOf(formularioChecklistIdAmb));

            String idPergunta = (String) resposta.get("idPergunta");
            Perguntas pergunta = perguntasService.getPerguntaById(Long.valueOf(idPergunta));

            Integer conformidade = Integer.parseInt((String) resposta.get("conformidade"));
            String observacoes = (String) resposta.get("observacoes");

            Respostas respostas1 = new Respostas();
            respostas1.setConformidade(conformidade);
            respostas1.setPergunta(pergunta);
            respostas1.setFormularioChecklists(formularioChecklistAmb);
            respostas1.setObservacoes(observacoes);
            respostasService.save(respostas1);

            if(conformidade == 3){
                formularioReprovarAmb = true;
            }else{
                if(conformidade == 2){
                    existeMedioAmb++;
                }
            }
        }

        //        Nota Ambiental
        if(formularioReprovarAmb){
            certificados.setNota_amb(3L);
        }else{
            if (existeMedioAmb > 1){
                certificados.setNota_amb(2L);
            }else{
                certificados.setNota_amb(1L);
            }
        }


        boolean formularioReprovarSoc = false;
        Integer existeMedioSoc = 0;
        for (Map<String, Object> resposta : respostasSoc) {
            FormularioChecklist formularioChecklistSoc = formularioChecklistService.getFormularioChecklistById(Long.valueOf(formularioChecklistIdSoc));

            String idPergunta = (String) resposta.get("idPergunta");
            Perguntas pergunta = perguntasService.getPerguntaById(Long.valueOf(idPergunta));

            Integer conformidade = Integer.parseInt((String) resposta.get("conformidade"));
            String observacoes = (String) resposta.get("observacoes");

            Respostas respostas1 = new Respostas();
            respostas1.setConformidade(conformidade);
            respostas1.setPergunta(pergunta);
            respostas1.setFormularioChecklists(formularioChecklistSoc);
            respostas1.setObservacoes(observacoes);
            respostasService.save(respostas1);

            if(conformidade == 3){
                formularioReprovarSoc = true;
            }else{
                if(conformidade == 2){
                    existeMedioSoc++;
                }
            }
        }

        //        Nota Social
        if(formularioReprovarSoc){
            certificados.setNota_soc(3L);
        }else{
            if (existeMedioSoc > 1){
                certificados.setNota_soc(2L);
            }else{
                certificados.setNota_soc(1L);
            }
        }

//        Aprovação de Certificado
        if(formularioReprovarGov || formularioReprovarAmb || formularioReprovarSoc){
            certificados.setAprovado(false);
        }else{
            certificados.setAprovado(existeMedioGov <= 1 || existeMedioAmb <= 1 || existeMedioSoc <= 1);
        }

        certificados.setFormulario(formularioService.getFormularioById(id));
        certificadosService.save(certificados);



        return "formularios/list";
    }

    public static String formatCNPJ(String cnpj) {
        return cnpj.replaceFirst("(\\d{2})(\\d{3})(\\d{3})(\\d{4})(\\d+)", "$1.$2.$3/$4-$5");
    }

}
