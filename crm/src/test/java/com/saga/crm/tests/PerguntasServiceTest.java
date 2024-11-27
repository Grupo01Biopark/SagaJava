package com.saga.crm.tests;

import com.saga.crm.model.Eixo;
import com.saga.crm.model.Perguntas;
import com.saga.crm.model.Porte;
import com.saga.crm.model.Setor;
import com.saga.crm.repositories.PerguntasRepository;
import com.saga.crm.service.PerguntasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PerguntasServiceTest {

    @Mock
    private PerguntasRepository perguntasRepository;

    @InjectMocks
    private PerguntasService perguntasService;

    private Perguntas pergunta;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        pergunta = new Perguntas();
        pergunta.setId(1L);
        pergunta.setDescricao("Pergunta de teste");
    }

    @Test
    public void testGetAllPerguntas() {
        when(perguntasRepository.findAll()).thenReturn(Arrays.asList(pergunta));
        assertEquals(1, perguntasService.getAllPerguntas().size());
    }

    @Test
    public void testSavePergunta() {
        Perguntas pergunta = new Perguntas();
        pergunta.setId(1L);
        pergunta.setTitulo("Título da Pergunta");
        pergunta.setDescricao("Qual a sua opinião sobre o produto?");
        pergunta.setImportante(1);
        pergunta.setAtiva(true);

        Eixo eixo = new Eixo();
        eixo.setId(2L);
        eixo.setTitulo("Eixo Teste");
        pergunta.setEixo(eixo);

        Porte porte = new Porte();
        porte.setId(3L);
        porte.setTitulo("Porte Teste");
        pergunta.setPorte(porte);

        Setor setor = new Setor();
        setor.setId(4L);
        setor.setTitulo("Setor Teste");
        pergunta.setSetor(setor);

        when(perguntasRepository.save(pergunta)).thenReturn(pergunta);

        perguntasService.save(pergunta);

        verify(perguntasRepository, times(1)).save(pergunta);
    }



    @Test
    public void testGetPerguntaById() {
        when(perguntasRepository.findById(1L)).thenReturn(Optional.of(pergunta));
        Perguntas result = perguntasService.getPerguntaById(1L);
        assertNotNull(result);
        assertEquals(pergunta.getId(), result.getId());
    }

    @Test
    public void testGetPerguntaByIdNotFound() {
        when(perguntasRepository.findById(1L)).thenReturn(Optional.empty());
        Perguntas result = perguntasService.getPerguntaById(1L);
        assertNull(result);
    }

    @Test
    public void testExcluirPergunta() {
        doNothing().when(perguntasRepository).deactivatePerguntaById(1L);
        perguntasService.excluirPergunta(1L);
        verify(perguntasRepository, times(1)).deactivatePerguntaById(1L);
    }

    @Test
    public void testFiltrarPerguntas() {
        when(perguntasRepository.findByEixoIdAndSetorIdAndPorteId(1L, 1L, 1L)).thenReturn(Arrays.asList(pergunta));
        assertEquals(1, perguntasService.filtrarPerguntas(1L, 1L, 1L).size());
    }

    @Test
    public void testFiltrarPerguntasSemFiltros() {
        when(perguntasRepository.findAll()).thenReturn(Arrays.asList(pergunta));
        assertEquals(1, perguntasService.filtrarPerguntas(null, null, null).size());
    }
}
