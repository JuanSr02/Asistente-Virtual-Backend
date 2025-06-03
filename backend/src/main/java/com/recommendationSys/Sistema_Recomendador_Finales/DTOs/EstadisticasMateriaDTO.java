package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDate;
import java.util.Map;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EstadisticasMateriaDTO {
    private String codigoMateria;
    private String nombreMateria;
    private Integer totalRendidos;
    private Integer aprobados;
    private Integer reprobados;
    private Double porcentajeAprobados;
    private Double promedioNotas;
    private Double promedioDiasEstudio;
    private Double promedioHorasDiarias;
    private Double promedioDificultad;

    private Map<Integer, Integer> distribucionDificultad;
    private Map<String, Integer> distribucionModalidad;
    private Map<String, Integer> distribucionRecursos;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fechaUltimaActualizacion;
}
