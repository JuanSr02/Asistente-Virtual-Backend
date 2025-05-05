package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@Data
@Entity
@Table(name = "Examen")
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String fecha;

    @Column(precision = 4, scale = 2)
    private BigDecimal nota;

    @OneToOne
    @JoinColumn(name = "Renglon_id", unique = true)
    private Renglon renglon;

    @OneToOne(mappedBy = "examen", cascade = CascadeType.ALL)
    private Experiencia experiencia;

    public Examen(String fecha, BigDecimal nota, Renglon renglon) {
        this.fecha = fecha;
        this.nota = nota;
        this.renglon = renglon;
    }
}

