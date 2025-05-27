package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Correlativa;
import com.recommendationSys.Sistema_Recomendador_Finales.model.CorrelativaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CorrelativaRepository extends JpaRepository<Correlativa, CorrelativaId> {

    @Query("SELECT COUNT(c) FROM Correlativa c WHERE c.correlativaCodigo.codigo = :codigoMateria")
    long countByCorrelativaCodigo(@Param("codigoMateria") String codigoMateria);
}
