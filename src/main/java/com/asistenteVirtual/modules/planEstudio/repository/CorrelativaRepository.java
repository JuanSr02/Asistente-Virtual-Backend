package com.asistenteVirtual.modules.planEstudio.repository;

import com.asistenteVirtual.modules.planEstudio.model.Correlativa;
import com.asistenteVirtual.modules.planEstudio.model.CorrelativaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorrelativaRepository extends JpaRepository<Correlativa, CorrelativaId> {

    long countByCorrelativaCodigo_CodigoAndCorrelativaCodigo_PlanDeEstudio_Codigo(String codigo, String planCodigo);
}
