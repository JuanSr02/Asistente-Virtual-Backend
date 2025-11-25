package com.asistenteVirtual.modules.historiaAcademica.model;

import com.asistenteVirtual.modules.planEstudio.model.Materia;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "renglon")
public class Renglon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false, length = 50)
    private String tipo; // Examen, Regularidad, Promocion, Equivalencia, En curso, Resolución

    private Double nota;

    @Column(nullable = false, length = 50)
    private String resultado; // Aprobado, Reprobado, Ausente

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "historia_id", nullable = false)
    private HistoriaAcademica historiaAcademica;

    // Relación con la Materia (Usando la clave compuesta o JoinColumns)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "materia_codigo", referencedColumnName = "codigo", nullable = false),
            @JoinColumn(name = "materia_plan_codigo", referencedColumnName = "PlanDeEstudio_codigo", nullable = false)
    })
    private Materia materia;

    @OneToOne(mappedBy = "renglon", cascade = CascadeType.ALL, orphanRemoval = true)
    private Examen examen;
}