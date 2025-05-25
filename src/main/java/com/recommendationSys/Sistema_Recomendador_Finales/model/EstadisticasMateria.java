package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "EstadisticasMateria")
@Data
public class EstadisticasMateria {
    @Id
    private String codigoMateria;

    private String nombreMateria;

    // Datos generales
    private Integer totalRendidos;
    private Integer aprobados;
    private Integer reprobados;
    private Double promedioNotas;

    // Sobre estudio
    private Double promedioDiasEstudio;
    private Double promedioHorasDiarias;
    private double promedioDificultad;


    // Distribuciones (guardadas como JSON)
    @Column(columnDefinition = "TEXT")
    private String distribucionDificultad; // Ej: {"1":5, "2":3, ... "10":20}

    @Column(columnDefinition = "TEXT")
    private String distribucionModalidad; // {"Oral":15, "Escrito":45}

    @Column(columnDefinition = "TEXT")
    private String distribucionRecursos; // {"Libros":30, "Apuntes":20, ...}

    // Otros campos estadísticos...

    @Column(name = "ultima_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;
}
