package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "Experiencia")
public class Experiencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "examen_id", unique = true)
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


    public Experiencia() {
    }

}