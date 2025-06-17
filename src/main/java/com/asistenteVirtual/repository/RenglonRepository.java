package com.asistenteVirtual.repository;

import com.asistenteVirtual.model.HistoriaAcademica;
import com.asistenteVirtual.model.Renglon;
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
