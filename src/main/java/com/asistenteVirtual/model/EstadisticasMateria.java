package com.asistenteVirtual.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "estadisticas_materia")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstadisticasMateria {

    @Id
    @Column(length = 15)
    private String codigoMateria;

    @Column(nullable = true, length = 200)
    private String nombreMateria;

    // Datos generales
    @Column(nullable = true)
    private Integer totalRendidos;

    @Column(nullable = true)
    private Integer aprobados;

    @Column(nullable = true)
    private Integer reprobados;

    @Column(nullable = true)
    private Double promedioNotas;

    // Sobre estudio
    @Column(nullable = true)
    private Double promedioDiasEstudio;

    @Column(nullable = true)
    private Double promedioHorasDiarias;

    @Column(nullable = true)
    private Double promedioDificultad;

    // Distribuciones (guardadas como JSON en texto plano)
    @Column(columnDefinition = "TEXT", nullable = true)
    private String distribucionDificultad; // Ej: {"1":5, "2":3, ... "10":20}

    @Column(columnDefinition = "TEXT", nullable = true)
    private String distribucionModalidad; // Ej: {"Oral":15, "Escrito":45}

    @Column(columnDefinition = "TEXT", nullable = true)
    private String distribucionRecursos; // Ej: {"Libros":30, "Apuntes":20, ...}

    @Column(name = "ultima_actualizacion", nullable = true)
    private LocalDateTime fechaUltimaActualizacion;

}
