package com.saga.crm.repositories;

import com.saga.crm.model.Perguntas;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PerguntasRepository extends JpaRepository<Perguntas, Long>{
    @Modifying
    @Transactional
    @Query("UPDATE Perguntas p SET p.ativa = false WHERE p.id = :id")
    void deactivatePerguntaById(Long id);

    @Query("SELECT p FROM Perguntas p " +
            "WHERE (:eixoId IS NULL OR p.eixo.id = :eixoId) " +
            "AND (:setorId IS NULL OR p.setor.id = :setorId) " +
            "AND (:porteId IS NULL OR p.porte.id = :porteId)")
    List<Perguntas> findByEixoIdAndSetorIdAndPorteId(
            @Param("eixoId") Long eixoId,
            @Param("setorId") Long setorId,
            @Param("porteId") Long porteId);



}
