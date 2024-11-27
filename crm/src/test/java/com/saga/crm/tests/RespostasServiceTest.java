package com.saga.crm.tests;

import com.saga.crm.model.Respostas;
import com.saga.crm.repositories.RespostasRepository;
import com.saga.crm.service.RespostasService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RespostasServiceTest {

    @Mock
    private RespostasRepository respostasRepository;

    @InjectMocks
    private RespostasService respostasService;

    private Respostas respostas;

    @BeforeEach
    public void setUp() {
        respostas = new Respostas();
        respostas.setId(1L);
        respostas.setConformidade(1);
        respostas.setObservacoes("Observação de teste");
        respostas.setPergunta(null);
        respostas.setFormularioChecklists(null);
    }

    @Test
    public void testSave() {
        when(respostasRepository.save(respostas)).thenReturn(respostas);
        respostasService.save(respostas);
        verify(respostasRepository, times(1)).save(respostas);
    }

    @Test
    public void testFindByCertificadoId() {
        when(respostasRepository.findByCertificadoId(1L)).thenReturn(Arrays.asList(respostas));
        List<Respostas> result = respostasService.findByCertificadoId(1L);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(respostas.getId(), result.get(0).getId());
    }
}
