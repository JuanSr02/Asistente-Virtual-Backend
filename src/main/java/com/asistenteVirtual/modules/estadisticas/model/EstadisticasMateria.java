package com.asistenteVirtual.modules.estadisticas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "estadisticas_materia")
public class EstadisticasMateria {

    @Id
    @Column(length = 15)
    private String codigoMateria;

    @Column(length = 200)
    private String nombreMateria;

    private Integer totalRendidos;
    private Integer aprobados;
    private Integer reprobados;
    private Double promedioNotas;

    private Double promedioDiasEstudio;
    private Double promedioHorasDiarias;
    private Double promedioDificultad;

    @Column(columnDefinition = "TEXT")
    private String distribucionDificultad;

    @Column(columnDefinition = "TEXT")
    private String distribucionModalidad;

    @Column(columnDefinition = "TEXT")
    private String distribucionRecursos;

    @Column(name = "ultima_actualizacion")
    private LocalDateTime fechaUltimaActualizacion;
}