package com.saga.crm.tests;

import com.saga.crm.model.*;
import com.saga.crm.repositories.ChecklistPerguntasRepository;
import com.saga.crm.service.ChecklistPerguntasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class ChecklistPerguntasServiceTest {

    @Mock
    private ChecklistPerguntasRepository checklistPerguntasRepository;

    @InjectMocks
    private ChecklistPerguntasService checklistPerguntasService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void save_ShouldSaveChecklistPergunta() {
        Eixo eixo = new Eixo(1L, "Eixo 1");
        Porte porte = new Porte(1L, "Porte 1");
        Setor setor = new Setor(1L, "Setor 1");

        Perguntas perguntas = new Perguntas();
        perguntas.setId(1L);
        perguntas.setTitulo("Pergunta Exemplo");
        perguntas.setDescricao("Descrição da pergunta");
        perguntas.setEixo(eixo);
        perguntas.setPorte(porte);
        perguntas.setSetor(setor);
        perguntas.setImportante(1);
        perguntas.setAtiva(true);

        Checklist checklist = new Checklist();
        checklist.setId(1L);
        checklist.setTitulo("Checklist Exemplo");

        ChecklistPerguntas checklistPerguntas = new ChecklistPerguntas();
        checklistPerguntas.setId(1L);
        checklistPerguntas.setChecklist(checklist);
        checklistPerguntas.setPerguntas(perguntas);

        checklistPerguntasService.save(checklistPerguntas);

        verify(checklistPerguntasRepository, times(1)).save(checklistPerguntas);
    }



    @Test
    void perguntasByChecklist_ShouldReturnPerguntasList_WhenIdExists() {
        Long checklistId = 1L;
        List<Object[]> expectedPerguntas = Arrays.asList(
                new Object[]{"Pergunta 1", "Resposta 1"},
                new Object[]{"Pergunta 2", "Resposta 2"}
        );
        when(checklistPerguntasRepository.findPerguntasByChecklistId(checklistId)).thenReturn(expectedPerguntas);

        List<Object[]> perguntas = checklistPerguntasService.perguntasByChecklist(checklistId);
        assertEquals(expectedPerguntas.size(), perguntas.size());
        assertEquals(expectedPerguntas, perguntas);
        verify(checklistPerguntasRepository, times(1)).findPerguntasByChecklistId(checklistId);
    }
}
