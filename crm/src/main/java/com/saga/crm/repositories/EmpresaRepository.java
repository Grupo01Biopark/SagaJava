package com.saga.crm.repositories;

import com.saga.crm.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    boolean existsByCnpj(String cnpj);

    @Query("SELECT e.porte.titulo, COUNT(e) FROM Empresa e GROUP BY e.porte.titulo")
    List<Object[]> countEmpresasByPorte();

    @Query("SELECT e.setor.titulo, COUNT(e) FROM Empresa e GROUP BY e.setor.titulo")
    List<Object[]> countEmpresasBySetor();

    @Query("SELECT YEAR(e.dataCadastro), MONTH(e.dataCadastro), COUNT(e) FROM Empresa e GROUP BY YEAR(e.dataCadastro), MONTH(e.dataCadastro)")
    List<Object[]> countEmpresasByDataCadastro();

    @Modifying
    @Query("UPDATE Empresa e SET e.ativa = false WHERE e.id = :id")
    void deactivateEmpresaById(Long id);
}
