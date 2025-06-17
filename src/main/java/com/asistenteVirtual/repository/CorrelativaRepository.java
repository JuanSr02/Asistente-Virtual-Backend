package com.asistenteVirtual.repository;

import com.asistenteVirtual.model.Correlativa;
import com.asistenteVirtual.model.CorrelativaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CorrelativaRepository extends JpaRepository<Correlativa, CorrelativaId> {

    long countByCorrelativaCodigo_CodigoAndCorrelativaCodigo_PlanDeEstudio_Codigo(String codigo, String planCodigo);
}
