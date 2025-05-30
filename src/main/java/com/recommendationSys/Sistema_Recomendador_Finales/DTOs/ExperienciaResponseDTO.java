package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ExperienciaResponseDTO {

    // Experiencia
    private Long id;
    private Integer dificultad;
    private Integer diasEstudio;
    private Integer horasDiarias;
    private Integer intentosPrevios;
    private String modalidad;
    private String recursos;
    private String motivacion;

    // Examen
    private LocalDate fechaExamen;
    private Double nota;

    // Materia
    private String codigoMateria;
    private String nombreMateria;
}
