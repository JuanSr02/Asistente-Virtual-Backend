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

    private Double nota;

    @OneToOne
    @JoinColumn(name = "Renglon_id", unique = true)
    private Renglon renglon;

    @OneToOne(mappedBy = "examen", cascade = CascadeType.ALL)
    private Experiencia experiencia;

    public Examen(String fecha, Double nota, Renglon renglon) {
        this.fecha = fecha;
        this.nota = nota;
        this.renglon = renglon;
    }

    public Examen() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public Renglon getRenglon() {
        return renglon;
    }

    public void setRenglon(Renglon renglon) {
        this.renglon = renglon;
    }

    public Experiencia getExperiencia() {
        return experiencia;
    }

    public void setExperiencia(Experiencia experiencia) {
        this.experiencia = experiencia;
    }
}

