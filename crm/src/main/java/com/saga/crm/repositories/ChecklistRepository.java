package com.saga.crm.repositories;

import com.saga.crm.model.Checklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ChecklistRepository extends JpaRepository<Checklist, Long> {

    List<Checklist> findByStatus(Integer status);

    @Query("SELECT DISTINCT c FROM Checklist c WHERE (:eixoId IS NULL OR c.eixo.id = :eixoId)")
    List<Checklist> findByEixo(@Param("eixoId") Integer eixoId);

    @Query("SELECT DISTINCT c FROM Checklist c INNER JOIN FormularioChecklist fc ON c.id = fc.checklist.id WHERE fc.formulario.id = :formularioId AND c.eixo.id = :eixoId")
    List<Checklist> findByFormularioIdAndEixo(@Param("formularioId") Long formularioId, @Param("eixoId") Long eixoId);
}
