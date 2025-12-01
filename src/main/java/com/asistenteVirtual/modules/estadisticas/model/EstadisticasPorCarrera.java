package com.asistenteVirtual.modules.estadisticas.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "estadisticas_por_carrera")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasPorCarrera {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String codigoPlan;

    @Column(nullable = false)
    private String periodo; // Enum como string: ULTIMO_ANIO, ULTIMO_SEMESTRE, TODOS

    @Column(nullable = false)
    private int estudiantesActivos;

    @Column(nullable = false)
    private int totalMaterias;

    @Column(nullable = false)
    private int totalExamenesRendidos;

    @Column(nullable = false)
    private double porcentajeAprobadosGeneral;

    @Column(nullable = false)
    private double promedioGeneral;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String distribucionExamenesPorMateria;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String top5Aprobadas;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String top5Reprobadas;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String promedioNotasPorMateria;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String materiaMasRendida;

    @Column(nullable = false)
    private long cantidadMateriaMasRendida;

    @Column(nullable = false)
    private LocalDateTime fechaUltimaActualizacion;
}