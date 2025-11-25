package com.asistenteVirtual.modules.experiencia.model;

import com.asistenteVirtual.modules.historiaAcademica.model.Examen;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "experiencia")
public class Experiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY) // Lazy para rendimiento
    @JoinColumn(name = "examen_id", unique = true, nullable = false)
    private Examen examen;

    @Column(nullable = false)
    private Integer dificultad;

    @Column(name = "dias_estudio", nullable = false)
    private Integer diasEstudio;

    @Column(name = "horas_diarias", nullable = false)
    private Integer horasDiarias;

    @Column(name = "intentos_previos", nullable = false)
    private Integer intentosPrevios;

    @Column(length = 20)
    private String modalidad;

    @Column(length = 200)
    private String recursos;

    @Column(length = 100)
    private String motivacion;

    private String linkResumen;
}