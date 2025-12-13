package com.asistenteVirtual.modules.planEstudio.repository;

import com.asistenteVirtual.modules.planEstudio.model.Correlativa;
import com.asistenteVirtual.modules.planEstudio.model.CorrelativaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CorrelativaRepository extends JpaRepository<Correlativa, CorrelativaId> {

    long countByCorrelativaCodigo_CodigoAndCorrelativaCodigo_PlanDeEstudio_Codigo(String codigo, String planCodigo);

    // Contar cuántas materias desbloquea cada materia de la lista (en un solo query)
    @Query("SELECT c.correlativaCodigo.codigo, COUNT(c) FROM Correlativa c " +
            "WHERE c.correlativaCodigo.codigo IN :codigos " +
            "AND c.correlativaCodigo.planDeEstudio.codigo = :planCodigo " +
            "GROUP BY c.correlativaCodigo.codigo")
    List<Object[]> contarCorrelativasFuturasMasivo(@Param("codigos") List<String> codigos,
                                                   @Param("planCodigo") String planCodigo);

}
