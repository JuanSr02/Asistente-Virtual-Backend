package com.asistenteVirtual.modules.inscripcion.model;

import com.asistenteVirtual.modules.estudiante.model.Estudiante;
import com.asistenteVirtual.modules.planEstudio.model.Materia;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "registro_inscripcion") // Mantenemos nombre original de tabla
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 50)
    private String turno;

    @Column(nullable = false)
    private Integer anio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "materia_codigo", referencedColumnName = "codigo", nullable = false),
            @JoinColumn(name = "materia_plan_codigo", referencedColumnName = "PlanDeEstudio_codigo", nullable = false)
    })
    private Materia materia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id_estudiante", nullable = false)
    private Estudiante estudiante;
}