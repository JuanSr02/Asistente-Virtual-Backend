package com.asistenteVirtual.modules.planEstudio.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "materia")
@IdClass(MateriaId.class)
public class Materia {

    @Id
    @Column(length = 15)
    private String codigo;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PlanDeEstudio_codigo", nullable = false)
    @JsonIgnore
    private PlanDeEstudio planDeEstudio;

    @Column(nullable = false, length = 200)
    private String nombre;

    @OneToMany(mappedBy = "materia", cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REFRESH})
    @JsonIgnore
    @Builder.Default
    private List<Correlativa> correlativas = new ArrayList<>();
}