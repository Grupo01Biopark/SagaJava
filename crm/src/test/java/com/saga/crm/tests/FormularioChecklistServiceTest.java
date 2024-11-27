package com.saga.crm.tests;

import com.saga.crm.model.Formulario;
import com.saga.crm.model.Checklist;
import com.saga.crm.model.FormularioChecklist;
import com.saga.crm.repositories.FormularioChecklistRepository;
import com.saga.crm.service.FormularioChecklistService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FormularioChecklistServiceTest {

    @Mock
    private FormularioChecklistRepository formularioChecklistRepository;

    @InjectMocks
    private FormularioChecklistService formularioChecklistService;

    private FormularioChecklist formularioChecklist;
    private Formulario formulario;
    private Checklist checklist;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        formulario = new Formulario();
        formulario.setId(1L);

        checklist = new Checklist();
        checklist.setId(1L);

        formularioChecklist = new FormularioChecklist();
        formularioChecklist.setId(1L);
        formularioChecklist.setFormulario(formulario);
        formularioChecklist.setChecklist(checklist);
    }

    @Test
    void save_ShouldSaveFormularioChecklist_WhenValid() {
        Formulario formulario = new Formulario();
        formulario.setId(1L);
        formulario.setTitulo("Formulario Teste");
        formulario.setDescricao("Descrição do Formulário Teste");
        formulario.setAtivo(true);

        Checklist checklist = new Checklist();
        checklist.setId(1L);
        checklist.setTitulo("Checklist Teste");

        FormularioChecklist formularioChecklist = new FormularioChecklist();
        formularioChecklist.setId(1L);
        formularioChecklist.setFormulario(formulario);
        formularioChecklist.setChecklist(checklist);

        when(formularioChecklistRepository.save(formularioChecklist)).thenReturn(formularioChecklist);

        formularioChecklistService.save(formularioChecklist);

        verify(formularioChecklistRepository, times(1)).save(formularioChecklist);
    }


    @Test
    void findByFormularioAndChecklist_ShouldReturnFormularioChecklist_WhenExists() {
        when(formularioChecklistRepository.findByFormularioIdAndChecklistId(1L, 1L)).thenReturn(formularioChecklist);

        FormularioChecklist result = formularioChecklistService.findByFormularioAndChecklist(1L, 1L);

        assertNotNull(result);
        assertEquals(formularioChecklist, result);
    }

    @Test
    void findByFormularioAndChecklist_ShouldReturnNull_WhenNotExists() {
        when(formularioChecklistRepository.findByFormularioIdAndChecklistId(1L, 1L)).thenReturn(null);

        FormularioChecklist result = formularioChecklistService.findByFormularioAndChecklist(1L, 1L);

        assertNull(result);
    }

    @Test
    void getFormularioChecklistById_ShouldReturnFormularioChecklist_WhenExists() {
        when(formularioChecklistRepository.findById(1L)).thenReturn(java.util.Optional.of(formularioChecklist));

        FormularioChecklist result = formularioChecklistService.getFormularioChecklistById(1L);

        assertNotNull(result);
        assertEquals(formularioChecklist, result);
    }

    @Test
    void getFormularioChecklistById_ShouldReturnNull_WhenNotExists() {
        when(formularioChecklistRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        FormularioChecklist result = formularioChecklistService.getFormularioChecklistById(1L);

        assertNull(result);
    }
}
