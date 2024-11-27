package com.saga.crm.tests;

import com.saga.crm.model.Certificados;
import com.saga.crm.repositories.CertificadosRepository;
import com.saga.crm.service.CertificadosService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CertificadosTestService {

    @Mock
    private CertificadosRepository certificadosRepository;

    @InjectMocks
    private CertificadosService certificadosService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveCertificados() {
        Certificados certificado = new Certificados();
        certificado.setId(1L);
        certificado.setAprovado(true);
        certificado.setData(LocalDateTime.now());

        when(certificadosRepository.save(any(Certificados.class))).thenReturn(certificado);

        certificadosService.save(certificado);

        verify(certificadosRepository, times(1)).save(certificado);
    }

    @Test
    void testGetAllCertificados() {
        List<Certificados> certificadosList = new ArrayList<>();
        Certificados certificado1 = new Certificados();
        certificado1.setId(1L);
        certificadosList.add(certificado1);

        Certificados certificado2 = new Certificados();
        certificado2.setId(2L);
        certificadosList.add(certificado2);

        when(certificadosRepository.findAll()).thenReturn(certificadosList);

        List<Certificados> result = certificadosService.getAllCertificados();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals(certificado1.getId(), result.get(0).getId());
        assertEquals(certificado2.getId(), result.get(1).getId());
        verify(certificadosRepository, times(1)).findAll();
    }

    @Test
    void testFindById_CertificadoExists() {
        Long id = 1L;
        Certificados certificado = new Certificados();
        certificado.setId(id);

        when(certificadosRepository.findById(id)).thenReturn(Optional.of(certificado));

        Certificados result = certificadosService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(certificadosRepository, times(1)).findById(id);
    }

    @Test
    void testFindById_CertificadoNotFound() {
        Long id = 1L;

        when(certificadosRepository.findById(id)).thenReturn(Optional.empty());

        Certificados result = certificadosService.findById(id);

        assertNull(result);
        verify(certificadosRepository, times(1)).findById(id);
    }
}
