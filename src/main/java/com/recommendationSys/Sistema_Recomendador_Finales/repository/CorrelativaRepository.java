package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Correlativa;
import com.recommendationSys.Sistema_Recomendador_Finales.model.CorrelativaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorrelativaRepository extends JpaRepository<Correlativa, CorrelativaId> {

    long countByCorrelativaCodigo_CodigoAndCorrelativaCodigo_PlanDeEstudio_Codigo(String codigo, String planCodigo);
}
