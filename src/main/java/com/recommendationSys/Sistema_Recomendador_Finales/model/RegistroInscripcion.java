package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "RegistroInscripcion")
public class RegistroInscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String turno;

    @Column(nullable = false)
    private Integer anio;

    @ManyToOne
    @JoinColumn(name = "materia_codigo", nullable = false)
    private Materia materia;

    @ManyToOne
    @JoinColumn(name = "Persona_id_estudiante", nullable = false)
    private Estudiante estudiante;
}


