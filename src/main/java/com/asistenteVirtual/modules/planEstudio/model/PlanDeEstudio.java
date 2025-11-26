package com.asistenteVirtual.modules.planEstudio.model;

import com.asistenteVirtual.modules.historiaAcademica.model.HistoriaAcademica;
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
@Table(name = "plan_de_estudio")
public class PlanDeEstudio {

    @Id
    @Column(length = 9)
    private String codigo;

    @Column(nullable = false, length = 150)
    private String propuesta;

    @OneToMany(mappedBy = "planDeEstudio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    @Builder.Default
    private List<Materia> materias = new ArrayList<>();

    @OneToMany(mappedBy = "planDeEstudio", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<HistoriaAcademica> historias;
}