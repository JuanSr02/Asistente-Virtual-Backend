package com.asistenteVirtual.DTOs;

import lombok.Builder;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public class EstadisticasGeneralesDTO {
    private Long estudiantesActivos;
    private Integer totalMaterias;
    private Integer totalExamenesRendidos;
    private Double porcentajeAprobadosGeneral;
    private Double promedioGeneral;
    private Map<String,Integer> distribucionEstudiantesPorCarrera;
    private Map<String,Integer> distribucionExamenesPorMateria;
    private MateriaRankingDTO materiaMasRendida;
    private Long cantidadMateriaMasRendida;
    private List<MateriaRankingDTO> top5Aprobadas;
    private List<MateriaRankingDTO> top5Reprobadas;
    private Map<String, Double> promedioNotasPorMateria;
}
