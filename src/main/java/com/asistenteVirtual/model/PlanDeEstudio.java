package com.asistenteVirtual.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan_de_estudio")
@Builder
public class PlanDeEstudio {

    @Id
    @Column(length = 9)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String propuesta; // nombre de la carrera

    @OneToMany(mappedBy = "planDeEstudio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    private List<Materia> materias = new ArrayList<>();

    @OneToMany(mappedBy = "planDeEstudio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @ToString.Exclude
    @Builder.Default
    private List<HistoriaAcademica> historias = new ArrayList<>();
}
