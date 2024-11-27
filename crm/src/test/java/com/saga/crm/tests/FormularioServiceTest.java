package com.saga.crm.tests;

import com.saga.crm.model.Formulario;
import com.saga.crm.repositories.FormularioRepository;
import com.saga.crm.service.FormularioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FormularioServiceTest {

    @Mock
    private FormularioRepository formularioRepository;

    @InjectMocks
    private FormularioService formularioService;

    private Formulario formulario;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        formulario = new Formulario();
        formulario.setId(1L);
        formulario.setTitulo("Formulario de Teste");
    }

    @Test
    void save_ShouldSaveFormulario_WhenValid() {
        Formulario formulario = new Formulario();
        formulario.setId(1L);
        formulario.setTitulo("Formulario Teste");
        formulario.setDescricao("Descrição do Formulário Teste");
        formulario.setAtivo(true);

        when(formularioRepository.save(formulario)).thenReturn(formulario);

        formularioService.save(formulario);

        verify(formularioRepository, times(1)).save(formulario);
    }

    @Test
    void getAllFormulario_ShouldReturnListOfFormulario() {
        when(formularioRepository.findAll()).thenReturn(Arrays.asList(formulario));

        var result = formularioService.getAllFormulario();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(formulario, result.get(0));
    }

    @Test
    void getFormularioById_ShouldReturnFormulario_WhenExists() {
        when(formularioRepository.findById(1L)).thenReturn(Optional.of(formulario));

        Formulario result = formularioService.getFormularioById(1L);

        assertNotNull(result);
        assertEquals(formulario, result);
    }

    @Test
    void getFormularioById_ShouldReturnNull_WhenNotExists() {
        when(formularioRepository.findById(1L)).thenReturn(Optional.empty());

        Formulario result = formularioService.getFormularioById(1L);

        assertNull(result);
    }

    @Test
    void excluirFormulario_ShouldDeactivateFormulario_WhenValidId() {
        doNothing().when(formularioRepository).deactivateFormularioById(1L);

        formularioService.excluirFormulario(1L);

        verify(formularioRepository, times(1)).deactivateFormularioById(1L);
    }
}
