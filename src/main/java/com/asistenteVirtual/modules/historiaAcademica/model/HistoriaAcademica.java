package com.asistenteVirtual.modules.historiaAcademica.model;

import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import com.asistenteVirtual.modules.planEstudio.model.PlanDeEstudio;
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
@Table(name = "historia_academica")
public class HistoriaAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id_estudiante", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_de_estudio_codigo", nullable = false)
    private PlanDeEstudio planDeEstudio;

    @Builder.Default
    @OneToMany(mappedBy = "historiaAcademica", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Renglon> renglones = new ArrayList<>();

    @Column(nullable = false)
    private String estado; // ACTIVA, BAJA

    // Helper para mantener la consistencia bidireccional
    public void agregarRenglon(Renglon renglon) {
        renglones.add(renglon);
        renglon.setHistoriaAcademica(this);
    }
}