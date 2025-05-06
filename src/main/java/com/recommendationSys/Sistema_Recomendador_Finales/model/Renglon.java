package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Renglon")
public class Renglon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 12)
    private String fecha;

    @Column(nullable = false, length = 50)
    private String tipo;

    private Double nota;

    @Column(nullable = false, length = 50)
    private String resultado;

    @ManyToOne
    @JoinColumn(name = "historia_id", nullable = false)
    private HistoriaAcademica historiaAcademica;

    @ManyToOne
    @JoinColumn(name = "materia_codigo", nullable = false)
    private Materia materia;

    @OneToOne(mappedBy = "renglon", cascade = CascadeType.ALL,orphanRemoval = true)
    private Examen examen;

    public Renglon(Long id, String fecha, String tipo, Double nota, String resultado, HistoriaAcademica historiaAcademica, Materia materia, Examen examen) {
        this.id = id;
        this.fecha = fecha;
        this.tipo = tipo;
        this.nota = nota;
        this.resultado = resultado;
        this.historiaAcademica = historiaAcademica;
        this.materia = materia;
        this.examen = examen;
    }

    public Renglon(String fecha, String tipo, Double nota, String resultado, HistoriaAcademica historiaAcademica, Materia materia) {
        this.fecha = fecha;
        this.tipo = tipo;
        this.nota = nota;
        this.resultado = resultado;
        this.historiaAcademica = historiaAcademica;
        this.materia = materia;
    }

    public Renglon() {
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

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public HistoriaAcademica getHistoriaAcademica() {
        return historiaAcademica;
    }

    public void setHistoriaAcademica(HistoriaAcademica historiaAcademica) {
        this.historiaAcademica = historiaAcademica;
    }

    public Materia getMateria() {
        return materia;
    }

    public void setMateria(Materia materia) {
        this.materia = materia;
    }

    public Examen getExamen() {
        return examen;
    }

    public void setExamen(Examen examen) {
        this.examen = examen;
    }
}


