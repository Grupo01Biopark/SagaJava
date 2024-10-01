package com.saga.crm.service;

import com.saga.crm.model.Respostas;
import com.saga.crm.repositories.RespostasRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RespostasService {
    private final RespostasRepository respostasRepository;

    public RespostasService(RespostasRepository respostasRepository) {
        this.respostasRepository = respostasRepository;
    }

    public void save(Respostas respostas) {
        respostasRepository.save(respostas);
    }

    public List<Respostas> findByCertificadoId(Long certId){
        return respostasRepository.findByCertificadoId(certId);
    }
}
