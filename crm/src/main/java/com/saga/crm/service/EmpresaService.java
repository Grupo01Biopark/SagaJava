package com.saga.crm.service;

import com.saga.crm.model.Empresa;
import com.saga.crm.repositories.EmpresaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Transactional
public class EmpresaService {

    private final EmpresaRepository empresaRepository;

    @Autowired
    public EmpresaService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public Empresa cadastrarEmpresa(Empresa empresa) {
        validarEmpresa(empresa);
        empresa.setCnpj(formatarCnpj(empresa.getCnpj())); // Formata o CNPJ antes de salvar
        return empresaRepository.save(empresa);
    }

    public List<Empresa> getAllEmpresas() {
        return empresaRepository.findAll();
    }

    public Empresa getEmpresaById(Long id) {
        return empresaRepository.findById(id).orElse(null);
    }

    public Empresa editarEmpresa(Empresa empresa) {
        validarEmpresaEditar(empresa);
        empresa.setCnpj(formatarCnpj(empresa.getCnpj())); // Formata o CNPJ antes de editar
        return empresaRepository.save(empresa);
    }

    public void excluirEmpresa(Long id) {
        empresaRepository.deactivateEmpresaById(id);
    }

    public boolean cnpjJaCadastrado(String cnpj) {
        return empresaRepository.existsByCnpj(cnpj);
    }

    private void validarEmpresa(Empresa empresa) {
        if (empresa == null ||
                empresa.getNomeFantasia() == null || empresa.getNomeFantasia().isEmpty() ||
                empresa.getCnpj() == null || empresa.getCnpj().isEmpty() ||
                empresa.getRazaoSocial() == null || empresa.getRazaoSocial().isEmpty() ||
                empresa.getLogradouro() == null || empresa.getLogradouro().isEmpty() ||
                empresa.getNumero() == null || empresa.getNumero().isEmpty() ||
                empresa.getCep() == null || empresa.getCep().isEmpty() ||
                empresa.getSetor() == null ||
                empresa.getPorte() == null) {
            throw new CamposObrigatoriosException("Todos os campos sao obrigatorios.");
        }

        if (!validarCnpj(empresa.getCnpj())) {
            throw new CNPJInvalidoException("CNPJ invalido.");
        }

        if (cnpjJaCadastrado(empresa.getCnpj())) {
            throw new CNPJJaCadastradoException("Este CNPJ já está cadastrado.");
        }
    }


    private void validarEmpresaEditar(Empresa empresa) {
        if (empresa == null ||
                empresa.getNomeFantasia() == null || empresa.getNomeFantasia().isEmpty() ||
                empresa.getCnpj() == null || empresa.getCnpj().isEmpty() ||
                empresa.getRazaoSocial() == null || empresa.getRazaoSocial().isEmpty() ||
                empresa.getLogradouro() == null || empresa.getLogradouro().isEmpty() ||
                empresa.getNumero() == null || empresa.getNumero().isEmpty() ||
                empresa.getCep() == null || empresa.getCep().isEmpty() ||
                empresa.getSetor() == null ||
                empresa.getPorte() == null) {
            throw new CamposObrigatoriosException("Todos os campos são obrigatórios.");
        }
    }

    private boolean validarCnpj(String cnpj) {
        cnpj = cnpj.replaceAll("[^0-9]", "");

        return cnpj.length() == 14 && !Pattern.matches("(\\d)\\1{13}", cnpj);
    }

    private String formatarCnpj(String cnpj) {
        return cnpj.replaceAll("[^0-9]", ""); // Remove a pontuação do CNPJ
    }

    public static class CamposObrigatoriosException extends RuntimeException {
        public CamposObrigatoriosException(String message) {
            super(message);
        }
    }

    public static class CNPJInvalidoException extends RuntimeException {
        public CNPJInvalidoException(String message) {
            super(message);
        }
    }

    public static class CNPJJaCadastradoException extends RuntimeException {
        public CNPJJaCadastradoException(String message) {
            super(message);
        }
    }
    public Map<String, Long> getEmpresasPorPorte() {
            List<Object[]> resultados = empresaRepository.countEmpresasByPorte();
            Map<String, Long> empresasPorPorte = new HashMap<>();
            for (Object[] resultado : resultados) {
                String tituloPorte = (String) resultado[0];
                Long quantidade = (Long) resultado[1];
                empresasPorPorte.put(tituloPorte, quantidade);
            }
            return empresasPorPorte;
    }
    public Map<String, Long> getEmpresasPorSetor() {
        List<Object[]> resultados = empresaRepository.countEmpresasBySetor();
        Map<String, Long> empresasPorSetor = new HashMap<>();
        for (Object[] resultado : resultados) {
            String tituloSetor = (String) resultado[0];
            Long quantidade = (Long) resultado[1];
            empresasPorSetor.put(tituloSetor, quantidade);
        }
        return empresasPorSetor;
    }
    public List<Object[]> countEmpresasByDataCadastro() {
        return empresaRepository.countEmpresasByDataCadastro();
    }
}
