package com.saga.crm.tests;

import com.saga.crm.model.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
public class MainApplicationTests {

    @Autowired
    private PerguntasTestService perguntasTestService;

    @Autowired
    private EixoTestService eixoTestService;
    @Autowired
    private SetorTestService setorTestService;
    @Autowired
    private PorteTestService porteTestService;

    @Test
    public void adicionarPergunta() {
//        cenario
        long id = 10L;
        String descricao = "descricao";
        String titulo = "titulo";
        String eixoTitulo = "Ambiental";
        String porteTitulo = "Microempresa";
        String setorTitulo = "T.I";

        Eixo eixo = new Eixo();
        eixo.setId(5L);
        eixo.setTitulo(eixoTitulo);

        Porte porte = new Porte();
        porte.setId(6L);
        porte.setTitulo(porteTitulo);

        Setor setor = new Setor();
        setor.setId(7L);
        setor.setTitulo(setorTitulo);

//        acao
        Perguntas pergunta = new Perguntas();
        pergunta.setId(id);
        pergunta.setDescricao(descricao);
        pergunta.setTitulo(titulo);
        pergunta.setEixo(eixo);
        pergunta.setPorte(porte);
        pergunta.setSetor(setor);

//        verificacao
        assert pergunta.getId().equals(10L);
        assert pergunta.getDescricao().equals(descricao);
        assert pergunta.getTitulo().equals(titulo);
        assert pergunta.getEixo().equals(eixo);
        assert pergunta.getPorte().equals(porte);
        assert pergunta.getSetor().equals(setor);


    }

    @Test
    public void testExcluirPergunta() {
        // cenário
        Long idPergunta = 10L;

        // ação
        perguntasTestService.excluirPergunta(idPergunta);

        // verificação
        Perguntas pergunta = perguntasTestService.getPerguntaById(idPergunta);
        assertNull(pergunta, "A pergunta deve ser nula após a exclusão");
    }

    @Test
    public void adicionarCertificado(){
        //cenario
        long idFormulario = 10L;
        String descricaoFormulario = "descricao";
        String tituloFormulario = "titulo";

        long idCertificado = 12L;
        Boolean aprovado = true;
        LocalDateTime data = LocalDateTime.now();
        long notaGov = 1L;
        long notaAmb = 1L;
        long notaSoc = 1L;

        //acao
        Formulario formulario = new Formulario();
        formulario.setId(idFormulario);
        formulario.setDescricao(descricaoFormulario);
        formulario.setTitulo(tituloFormulario);

        Certificados certificado = new Certificados();
        certificado.setId(idCertificado);
        certificado.setAprovado(aprovado);
        certificado.setData(data);
        certificado.setNota_gov(notaGov);
        certificado.setNota_amb(notaAmb);
        certificado.setNota_soc(notaSoc);
        certificado.setFormulario(formulario);

        //verificacao
        assert certificado.getId().equals(12L);
        assert certificado.isAprovado();
        assert certificado.getData().equals(data);
        assert certificado.getNota_gov().equals(notaGov);
        assert certificado.getNota_amb().equals(notaAmb);
        assert certificado.getNota_soc().equals(notaSoc);
        assert certificado.getFormulario().equals(formulario);
    }
}