package com.asistenteVirtual.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "estadisticas_generales")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EstadisticasGenerales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long estudiantesActivos;

    @Column(nullable = false)
    private Integer totalMaterias;

    @Column(nullable = false)
    private Integer totalExamenesRendidos;

    @Column(nullable = false)
    private Double porcentajeAprobadosGeneral;

    @Column(nullable = false)
    private Double promedioGeneral;

    // Distribuciones guardadas como JSON
    @Column(columnDefinition = "TEXT", nullable = false)
    private String distribucionEstudiantesPorCarrera;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String distribucionExamenesPorMateria;

    // Datos de rankings
    @Column(columnDefinition = "TEXT", nullable = false)
    private String top5Aprobadas;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String top5Reprobadas;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String materiaMasRendida;

    @Column(nullable = false)
    private Long cantidadMateriaMasRendida;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String promedioNotasPorMateria;

    @Column(name = "ultima_actualizacion", nullable = false)
    private LocalDateTime fechaUltimaActualizacion;
}
