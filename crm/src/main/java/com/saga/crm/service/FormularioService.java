package com.saga.crm.service;

import com.saga.crm.model.Formulario;
import com.saga.crm.repositories.FormularioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FormularioService {
    private final FormularioRepository formularioRepository;

    public FormularioService(FormularioRepository formularioRepository) {
        this.formularioRepository = formularioRepository;
    }

    public void save(Formulario formulario) {
        formularioRepository.save(formulario);
    }

    public List<Formulario> getAllFormulario() {
        return formularioRepository.findAll();
    }

    public Formulario getFormularioById(Long id) {
        return formularioRepository.findById(id).orElse(null);
    }

    public void excluirFormulario(Long id) {
        formularioRepository.deactivateFormularioById(id);
    }
}
