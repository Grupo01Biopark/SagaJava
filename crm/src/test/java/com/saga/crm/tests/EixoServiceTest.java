package com.saga.crm.tests;

import com.saga.crm.model.Eixo;
import com.saga.crm.repositories.EixoRepository;
import com.saga.crm.service.EixoService;
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

class EixoServiceTest {

    @Mock
    private EixoRepository eixoRepository;

    @InjectMocks
    private EixoService eixoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllEixos_ShouldReturnAllEixos() {
        List<Eixo> expectedEixos = Arrays.asList(new Eixo(), new Eixo());
        when(eixoRepository.findAll()).thenReturn(expectedEixos);

        List<Eixo> result = eixoService.getAllEixos();

        assertEquals(expectedEixos, result);
        verify(eixoRepository, times(1)).findAll();
    }

    @Test
    void getEixoById_ShouldReturnEixo_WhenIdExists() {
        Long eixoId = 1L;
        Eixo eixo = new Eixo();
        when(eixoRepository.findById(eixoId)).thenReturn(Optional.of(eixo));

        Eixo result = eixoService.getEixoById(eixoId);

        assertEquals(eixo, result);
        verify(eixoRepository, times(1)).findById(eixoId);
    }

    @Test
    void getEixoById_ShouldReturnNull_WhenIdDoesNotExist() {
        Long eixoId = 1L;
        when(eixoRepository.findById(eixoId)).thenReturn(Optional.empty());

        Eixo result = eixoService.getEixoById(eixoId);

        assertNull(result);
        verify(eixoRepository, times(1)).findById(eixoId);
    }

    @Test
    void findEixoByTitulo_ShouldReturnEixo_WhenTituloExists() {
        String titulo = "Example Title";
        Eixo eixo = new Eixo();
        when(eixoRepository.findByTitulo(titulo)).thenReturn(eixo);

        Eixo result = eixoService.findEixoByTitulo(titulo);

        assertEquals(eixo, result);
        verify(eixoRepository, times(1)).findByTitulo(titulo);
    }

    @Test
    void findEixoByTitulo_ShouldReturnNull_WhenTituloDoesNotExist() {
        String titulo = "Non-Existent Title";
        when(eixoRepository.findByTitulo(titulo)).thenReturn(null);

        Eixo result = eixoService.findEixoByTitulo(titulo);

        assertNull(result);
        verify(eixoRepository, times(1)).findByTitulo(titulo);
    }

    @Test
    void save_ShouldSaveEixo() {
        Eixo eixo = new Eixo();
        eixo.setId(1L);
        eixo.setTitulo("Eixo de Teste");

        when(eixoRepository.save(eixo)).thenReturn(eixo);

        Eixo result = eixoService.save(eixo);

        assertEquals(eixo, result);
        verify(eixoRepository, times(1)).save(eixo);
    }
}
