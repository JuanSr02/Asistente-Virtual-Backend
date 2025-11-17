package com.asistenteVirtual.repository;

import com.asistenteVirtual.model.HistoriaAcademica;
import com.asistenteVirtual.model.Renglon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RenglonRepository extends JpaRepository<Renglon, Long> {

    long countByHistoriaAcademica(HistoriaAcademica historiaAcademica);

    List<Renglon> findByHistoriaAcademicaAndTipoAndResultado(
            HistoriaAcademica historiaAcademica, String tipo, String resultado);


    List<Renglon> findByHistoriaAcademica(HistoriaAcademica historiaAcademica);

    /**
     * Devuelve los renglones de 'Regularidad' Aprobada cuya materia NO tiene algun
     * prerrequisitos (correlativas) también con estado 'Regularidad' Aprobada.
     *
     */
    @Query(value = """
            SELECT
                r_outer.*
            FROM
                public.renglon AS r_outer
            WHERE
                r_outer.historia_id = :historiaId
                AND r_outer.tipo = 'Regularidad'
                AND r_outer.resultado = 'Aprobado'
            
                AND NOT EXISTS (
                    SELECT
                        1
                    FROM
                        public.correlativa AS c
                    INNER JOIN
                        public.renglon AS r_inner
                        ON c.correlativa_codigo = r_inner.materia_codigo
                        AND c.correlativa_plan_codigo = r_inner.materia_plan_codigo
                    WHERE
                        c.materia_codigo = r_outer.materia_codigo
                        AND c.materia_plan_codigo = r_outer.materia_plan_codigo
            
                        AND r_inner.historia_id = :historiaId
                        AND r_inner.tipo = 'Regularidad'
                        AND r_inner.resultado = 'Aprobado'
                )
            ORDER BY
                r_outer.fecha DESC;
            """, nativeQuery = true)
    List<Renglon> findRegularesHabilitadas(
            @Param("historiaId") Long historiaId
    );

}
