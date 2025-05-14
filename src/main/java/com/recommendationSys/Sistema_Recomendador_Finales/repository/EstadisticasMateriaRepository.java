package com.recommendationSys.Sistema_Recomendador_Finales.repository;

import com.recommendationSys.Sistema_Recomendador_Finales.model.EstadisticasMateria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EstadisticasMateriaRepository extends JpaRepository<EstadisticasMateria, String> {

    @Query(nativeQuery = true, value = """
        WITH stats AS (
          SELECT 
            m.codigo,
            m.nombre,
            COUNT(e.id) as total_rendidos,
            AVG(e.nota) as promedio_notas,
            SUM(CASE WHEN e.nota >= 4 THEN 1 ELSE 0 END) as aprobados
          FROM materias m
          LEFT JOIN examenes e ON m.codigo = e.materia_codigo
          GROUP BY m.codigo, m.nombre
        )
        SELECT * FROM stats
        """)
    List<EstadisticasProjection> getEstadisticasBasicas();

    public interface EstadisticasProjection {
        String getCodigo();
        String getNombre();
        Long getTotalRendidos();
        Double getPromedioNotas();
        Long getAprobados();
    }
}
