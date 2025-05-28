package com.recommendationSys.Sistema_Recomendador_Finales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "materia")
@Builder
@IdClass(MateriaId.class)
public class Materia {

    @Id
    @Column(length = 15)
    private String codigo;

    @Column(nullable = false, length = 200)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlanDeEstudio_codigo", nullable = false)
    @ToString.Exclude
    @Id
    private PlanDeEstudio planDeEstudio;

    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<Correlativa> correlativas = new ArrayList<>();

    @OneToMany(mappedBy = "correlativaCodigo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Correlativa> correlativasQueLaReferencian = new ArrayList<>();

    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<Renglon> renglones = new ArrayList<>();

    @OneToMany(mappedBy = "materia", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    @ToString.Exclude
    private List<RegistroInscripcion> inscripciones = new ArrayList<>();
}
