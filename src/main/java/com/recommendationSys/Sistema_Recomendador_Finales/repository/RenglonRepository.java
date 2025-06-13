package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Renglon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RenglonRepository extends JpaRepository<Renglon, Long> {

    long countByHistoriaAcademica(HistoriaAcademica historiaAcademica);

    List<Renglon> findByHistoriaAcademicaAndTipoAndResultado(
            HistoriaAcademica historiaAcademica, String tipo, String resultado);


    List<Renglon> findByHistoriaAcademica(HistoriaAcademica historiaAcademica);

}
