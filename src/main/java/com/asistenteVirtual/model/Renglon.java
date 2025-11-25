package com.asistenteVirtual.model;

import com.asistenteVirtual.modules.planEstudio.model.Materia;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Data
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
    private String tipo;

    private Double nota;

    @Column(nullable = false, length = 50)
    private String resultado;

    @ManyToOne
    @JoinColumn(name = "historia_id", nullable = false)
    @ToString.Exclude
    private HistoriaAcademica historiaAcademica;

    @ManyToOne
    @JoinColumns({
            @JoinColumn(name = "materia_codigo", referencedColumnName = "codigo", nullable = false),
            @JoinColumn(name = "materia_plan_codigo", referencedColumnName = "PlanDeEstudio_codigo", nullable = false)
    })
    private Materia materia;

    @OneToOne(mappedBy = "renglon", cascade = CascadeType.ALL, orphanRemoval = true)
    private Examen examen;

    @PrePersist
    @PreUpdate
    private void validarFecha() {
        if (fecha != null && fecha.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("La fecha no puede ser posterior a la actual");
        }
    }
}
