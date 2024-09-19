package com.saga.crm.service;

import com.saga.crm.model.Perguntas;
import com.saga.crm.repositories.PerguntasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PerguntasService {

    private final PerguntasRepository perguntasRepository;

    @Autowired
    public PerguntasService(PerguntasRepository perguntasRepository) {
        this.perguntasRepository = perguntasRepository;
    }

    public List<Perguntas> getAllPerguntas() {
        return perguntasRepository.findAll();
    }

    public void save(Perguntas pergunta) {
        perguntasRepository.save(pergunta);
    }

    public Perguntas getPerguntaById(Long id) {
        Optional<Perguntas> optionalPergunta = perguntasRepository.findById(id);
        return optionalPergunta.orElse(null);
    }

    public void excluirPergunta(Long id) {
        perguntasRepository.deactivatePerguntaById(id);
    }

    public List<Perguntas> filtrarPerguntas(Long eixoId, Long setorId, Long porteId) {
        if (eixoId == null && setorId == null && porteId == null) {
            return getAllPerguntas();
        } else {
            return perguntasRepository.findByEixoIdAndSetorIdAndPorteId(eixoId, setorId, porteId);
        }
    }

}
