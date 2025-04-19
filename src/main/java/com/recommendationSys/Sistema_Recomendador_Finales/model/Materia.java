package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "Materia")
public class Materia {

    @Id
    @Column(length = 15)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "PlanDeEstudio_codigo")
    private PlanDeEstudio planDeEstudio;

    @OneToMany(mappedBy = "materia")
    private List<Correlativa> correlativas;

    @OneToMany(mappedBy = "correlativa")
    private List<Correlativa> esCorrelavitaDe;

    @OneToMany(mappedBy = "materia")
    private List<Renglon> renglones;

    @OneToMany(mappedBy = "materia")
    private List<RegistroInscripcion> inscripciones;
}

