package com.saga.crm.repositories;

import com.saga.crm.model.Formulario;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FormularioRepository extends JpaRepository<Formulario, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Formulario f SET f.ativo = false WHERE f.id = :id")
    void deactivateFormularioById(Long id);
}
