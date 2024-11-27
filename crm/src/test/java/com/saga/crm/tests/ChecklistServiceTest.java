package com.saga.crm.tests;

import com.saga.crm.model.Checklist;
import com.saga.crm.model.Eixo;
import com.saga.crm.model.Perguntas;
import com.saga.crm.model.Setor;
import com.saga.crm.repositories.ChecklistPerguntasRepository;
import com.saga.crm.repositories.ChecklistRepository;
import com.saga.crm.service.ChecklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

class ChecklistServiceTest {

    @Mock
    private ChecklistRepository checklistRepository;

    @Mock
    private ChecklistPerguntasRepository checklistPerguntasRepository;

    @InjectMocks
    private ChecklistService checklistService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getChecklistById_ShouldReturnChecklist_WhenIdExists() {
        Long checklistId = 1L;
        Checklist checklist = new Checklist();
        when(checklistRepository.findById(checklistId)).thenReturn(Optional.of(checklist));

        Checklist result = checklistService.getChecklistById(checklistId);

        assertEquals(checklist, result);
        verify(checklistRepository, times(1)).findById(checklistId);
    }

    @Test
    void getChecklistById_ShouldReturnNull_WhenIdDoesNotExist() {
        Long checklistId = 1L;
        when(checklistRepository.findById(checklistId)).thenReturn(Optional.empty());

        Checklist result = checklistService.getChecklistById(checklistId);

        assertNull(result);
        verify(checklistRepository, times(1)).findById(checklistId);
    }

    @Test
    void getAllChecklists_ShouldReturnAllChecklists() {
        List<Checklist> expectedChecklists = Arrays.asList(new Checklist(), new Checklist());
        when(checklistRepository.findAll()).thenReturn(expectedChecklists);

        List<Checklist> result = checklistService.getAllChecklists();

        assertEquals(expectedChecklists, result);
        verify(checklistRepository, times(1)).findAll();
    }

    @Test
    void getActiveChecklists_ShouldReturnActiveChecklists() {
        List<Checklist> activeChecklists = Arrays.asList(new Checklist(), new Checklist());
        when(checklistRepository.findByStatus(1)).thenReturn(activeChecklists);

        List<Checklist> result = checklistService.getActiveChecklists();

        assertEquals(activeChecklists, result);
        verify(checklistRepository, times(1)).findByStatus(1);
    }

    @Test
    void save_ShouldSaveChecklist() {
        Eixo eixo = new Eixo(1L, "Eixo de Teste");
        Checklist checklist = new Checklist();
        checklist.setStatus(1);
        checklist.setEixo(eixo);

        checklistService.save(checklist);

        verify(checklistRepository, times(1)).save(checklist);

    }

    @Test
    void getChecklistByEixo_ShouldReturnChecklistsByEixo() {
        Integer eixoId = 1;
        List<Checklist> checklistsByEixo = Arrays.asList(new Checklist(), new Checklist());
        when(checklistRepository.findByEixo(eixoId)).thenReturn(checklistsByEixo);

        List<Checklist> result = checklistService.getChecklistByEixo(eixoId);

        assertEquals(checklistsByEixo, result);
        verify(checklistRepository, times(1)).findByEixo(eixoId);
    }

    @Test
    void getChecklistByFormularioIdAndEixo_ShouldReturnChecklistsByFormularioAndEixo() {
        Long formularioId = 1L;
        Integer eixoId = 2;
        List<Checklist> expectedChecklists = Arrays.asList(new Checklist(), new Checklist());
        when(checklistRepository.findByFormularioIdAndEixo(formularioId, Long.valueOf(eixoId))).thenReturn(expectedChecklists);

        List<Checklist> result = checklistService.getChecklistByFormularioIdAndEixo(formularioId, eixoId);

        assertEquals(expectedChecklists, result);
        verify(checklistRepository, times(1)).findByFormularioIdAndEixo(formularioId, Long.valueOf(eixoId));
    }

    @Test
    void getChecklistPerguntasById_ShouldReturnPerguntasList_WhenChecklistIdExists() {
        Long checklistId = 1L;
        List<Perguntas> expectedPerguntas = Arrays.asList(new Perguntas(), new Perguntas());
        when(checklistPerguntasRepository.findPerguntasByChecklistsId(checklistId)).thenReturn(expectedPerguntas);

        List<Perguntas> result = checklistService.getChecklistPerguntasById(checklistId);

        assertEquals(expectedPerguntas, result);
        verify(checklistPerguntasRepository, times(1)).findPerguntasByChecklistsId(checklistId);
    }
}
