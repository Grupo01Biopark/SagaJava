package com.saga.crm.tests;

import com.saga.crm.service.PorteService;
import com.saga.crm.model.Porte;
import com.saga.crm.repositories.PorteRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PorteServiceTest {

    @Mock
    private PorteRepository porteRepository;

    @InjectMocks
    private PorteService porteService;

    private Porte porte;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        porte = new Porte();
        porte.setId(1L);
        porte.setTitulo("Porte de teste");
    }

    @Test
    public void testGetAllPortes() {
        when(porteRepository.findAll()).thenReturn(Arrays.asList(porte));
        assertEquals(1, porteService.getAllPortes().size());
    }

    @Test
    public void testGetPorteById() {
        when(porteRepository.findById(1L)).thenReturn(Optional.of(porte));
        Porte result = porteService.getPorteById(1L);
        assertNotNull(result);
        assertEquals(porte.getId(), result.getId());
    }

    @Test
    public void testGetPorteByIdNotFound() {
        when(porteRepository.findById(1L)).thenReturn(Optional.empty());
        Porte result = porteService.getPorteById(1L);
        assertNull(result);
    }

    @Test
    public void testFindPorteByTitulo() {
        when(porteRepository.findPorteByTitulo("Porte de teste")).thenReturn(porte);
        Porte result = porteService.findPorteByTitulo("Porte de teste");
        assertNotNull(result);
        assertEquals(porte.getTitulo(), result.getTitulo());
    }

    @Test
    public void testSavePorte() {
        when(porteRepository.save(porte)).thenReturn(porte);
        porteService.save(porte);
        verify(porteRepository, times(1)).save(porte);
    }
}
