package com.saga.crm.tests;

import com.saga.crm.model.Empresa;
import com.saga.crm.model.Porte;
import com.saga.crm.model.Setor;
import com.saga.crm.repositories.EmpresaRepository;
import com.saga.crm.service.EmpresaService;
import com.saga.crm.service.EmpresaService.CNPJInvalidoException;
import com.saga.crm.service.EmpresaService.CNPJJaCadastradoException;
import com.saga.crm.service.EmpresaService.CamposObrigatoriosException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @InjectMocks
    private EmpresaService empresaService;

    private Empresa empresa;

    @BeforeEach
    public void setUp() {
        empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNomeFantasia("Empresa Teste");
        empresa.setCnpj("12345678000195");
        empresa.setRazaoSocial("Razão Social Teste");
        empresa.setLogradouro("Rua Teste");
        empresa.setNumero("123");
        empresa.setCep("12345000");
        empresa.setEmail("teste@empresa.com");

        // Adicionando Setor e Porte se forem obrigatórios
        Setor setor = new Setor(1L, "Setor Teste");
        Porte porte = new Porte(1L, "Porte Teste");
        empresa.setSetor(setor);
        empresa.setPorte(porte);
    }

    @Test
    public void testCadastrarEmpresa() {
        when(empresaRepository.existsByCnpj(anyString())).thenReturn(false);
        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);

        Empresa resultado = empresaService.cadastrarEmpresa(empresa);

        assertNotNull(resultado);
        assertEquals("12345678000195", resultado.getCnpj());
        verify(empresaRepository, times(1)).save(empresa);
    }

    @Test
    public void testCadastrarEmpresa_CnpjDuplicado() {
        when(empresaRepository.existsByCnpj(anyString())).thenReturn(true);

        assertThrows(CNPJJaCadastradoException.class, () -> empresaService.cadastrarEmpresa(empresa));
        verify(empresaRepository, never()).save(empresa);
    }

    @Test
    public void testCadastrarEmpresa_CnpjInvalido() {
        empresa.setCnpj("11111111111111");

        assertThrows(CNPJInvalidoException.class, () -> empresaService.cadastrarEmpresa(empresa));
        verify(empresaRepository, never()).save(empresa);
    }

    @Test
    public void testCadastrarEmpresa_CamposObrigatoriosFaltando() {
        empresa.setNomeFantasia(null);

        assertThrows(CamposObrigatoriosException.class, () -> empresaService.cadastrarEmpresa(empresa));
        verify(empresaRepository, never()).save(empresa);
    }

    @Test
    public void testGetAllEmpresas() {
        List<Empresa> empresas = new ArrayList<>();
        empresas.add(empresa);

        when(empresaRepository.findAll()).thenReturn(empresas);

        List<Empresa> resultado = empresaService.getAllEmpresas();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(empresaRepository, times(1)).findAll();
    }

    @Test
    public void testGetEmpresaById() {
        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));

        Empresa resultado = empresaService.getEmpresaById(1L);

        assertNotNull(resultado);
        assertEquals("Empresa Teste", resultado.getNomeFantasia());
        verify(empresaRepository, times(1)).findById(1L);
    }

    @Test
    public void testEditarEmpresa() {
        // Configuração do Porte e Setor
        Porte porte = new Porte(1L, "Porte 1");
        Setor setor = new Setor(1L, "Setor 1");

        // Atualizando os dados da empresa
        empresa.setNomeFantasia("Empresa Editada");
        empresa.setCnpj("12345678000195");
        empresa.setRazaoSocial("Razão Social Editada");
        empresa.setLogradouro("Rua Editada");
        empresa.setNumero("456");
        empresa.setCep("54321000");
        empresa.setSetor(setor);
        empresa.setPorte(porte);

        when(empresaRepository.save(any(Empresa.class))).thenReturn(empresa);

        Empresa resultado = empresaService.editarEmpresa(empresa);

        assertNotNull(resultado);
        assertEquals("12345678000195", resultado.getCnpj());
        assertEquals("Empresa Editada", resultado.getNomeFantasia());

        verify(empresaRepository, times(1)).save(empresa);
    }


    @Test
    public void testExcluirEmpresa() {
        doNothing().when(empresaRepository).deactivateEmpresaById(1L);

        empresaService.excluirEmpresa(1L);

        verify(empresaRepository, times(1)).deactivateEmpresaById(1L);
    }
}
