package com.asistenteVirtual.modules.planEstudio.repository;

import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanDeEstudioRepository extends JpaRepository<PlanDeEstudio, String> {

}

