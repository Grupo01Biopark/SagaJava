package com.saga.crm.repositories;

import com.saga.crm.model.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    boolean existsByCnpj(String cnpj);

    @Query("SELECT e.porte.titulo, COUNT(e) FROM Empresa e GROUP BY e.porte.titulo")
    List<Object[]> countEmpresasByPorte();

    @Query("SELECT e.setor.titulo, COUNT(e) FROM Empresa e GROUP BY e.setor.titulo")
    List<Object[]> countEmpresasBySetor();
}
