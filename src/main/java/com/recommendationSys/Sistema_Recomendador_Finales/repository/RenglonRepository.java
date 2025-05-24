package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
import com.recommendationSys.Sistema_Recomendador_Finales.model.Renglon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RenglonRepository extends JpaRepository<Renglon, Long> {

    boolean existsByMateriaAndHistoriaAcademicaAndTipoAndNotaGreaterThanEqual(Materia materia,HistoriaAcademica historiaAcademica, String tipo, double nota);

    boolean existsByMateriaAndHistoriaAcademicaAndTipoAndResultado(Materia materia,HistoriaAcademica historiaAcademica, String Tipo,String resultado);

    Optional<Renglon> findByMateriaAndHistoriaAcademicaAndTipoAndResultado(Materia materia, HistoriaAcademica historiaAcademica, String tipo, String resultado);

    void deleteByTipoAndResultado(String Tipo,String Resultado);

    List<Renglon> findByHistoriaAcademicaAndTipoAndResultado(
            HistoriaAcademica historiaAcademica, String tipo, String resultado);

}
