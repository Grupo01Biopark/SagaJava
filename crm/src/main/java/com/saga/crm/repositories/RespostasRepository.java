package com.saga.crm.repositories;

import com.saga.crm.model.Respostas;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RespostasRepository extends JpaRepository<Respostas, Long> {
    List<Respostas> findByCertificadoId(Long certificadoId);
}
