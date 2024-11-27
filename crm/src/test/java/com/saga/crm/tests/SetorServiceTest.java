package com.saga.crm.tests;

import com.saga.crm.model.Setor;
import com.saga.crm.repositories.SetorRepository;
import com.saga.crm.service.SetorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SetorServiceTest {

    @Mock
    private SetorRepository setorRepository;

    @InjectMocks
    private SetorService setorService;

    private Setor setor;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        setor = new Setor();
        setor.setId(1L);
        setor.setTitulo("Setor de Teste");
    }

    @Test
    public void testGetAllSetores() {
        when(setorRepository.findAll()).thenReturn(Arrays.asList(setor));

        assertEquals(1, setorService.getAllSetores().size());

        verify(setorRepository, times(1)).findAll();
    }

    @Test
    public void testGetSetorById() {
        when(setorRepository.findById(1L)).thenReturn(Optional.of(setor));

        Setor result = setorService.getSetorById(1L);

        assertNotNull(result);
        assertEquals(setor.getId(), result.getId());
        assertEquals(setor.getTitulo(), result.getTitulo());

        verify(setorRepository, times(1)).findById(1L);
    }

    @Test
    public void testGetSetorByIdNotFound() {
        when(setorRepository.findById(1L)).thenReturn(Optional.empty());

        Setor result = setorService.getSetorById(1L);

        assertNull(result);

        verify(setorRepository, times(1)).findById(1L);
    }

    @Test
    public void testFindSetorByTitulo() {
        when(setorRepository.findByTitulo("Setor de Teste")).thenReturn(setor);

        Setor result = setorService.findSetorByTitulo("Setor de Teste");

        assertNotNull(result);
        assertEquals(setor.getTitulo(), result.getTitulo());

        verify(setorRepository, times(1)).findByTitulo("Setor de Teste");
    }

    @Test
    public void testSaveSetor() {
        when(setorRepository.save(setor)).thenReturn(setor);

        Setor result = setorService.save(setor);

        assertNotNull(result);
        assertEquals(setor.getId(), result.getId());
        assertEquals(setor.getTitulo(), result.getTitulo());

        verify(setorRepository, times(1)).save(setor);
    }
}
