package com.recommendationSys.Sistema_Recomendador_Finales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    @ToString.Exclude
    private HistoriaAcademica historiaAcademica;

    @ManyToOne
    @JoinColumn(name = "materia_codigo", nullable = false)
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

    public Renglon(LocalDate fecha, String tipo, Double nota, String resultado, HistoriaAcademica historiaAcademica, Materia materia) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.nota = nota;
        this.resultado = resultado;
        this.historiaAcademica = historiaAcademica;
        this.materia = materia;
    }
}
