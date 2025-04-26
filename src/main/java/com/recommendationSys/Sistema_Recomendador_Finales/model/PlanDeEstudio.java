package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "PlanDeEstudio")
public class PlanDeEstudio {

    @Id
    @Column(length = 9)
    private String codigo;

    @Column(nullable = false, length = 30)
    private String propuesta;

    @OneToMany(mappedBy = "planDeEstudio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Materia> materias = new ArrayList<>();

    public PlanDeEstudio(String codigo, String propuesta, List<Materia> materias) {
        this.codigo = codigo;
        this.propuesta = propuesta;
        this.materias = materias;
    }

    public PlanDeEstudio() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getPropuesta() {
        return propuesta;
    }

    public void setPropuesta(String propuesta) {
        this.propuesta = propuesta;
    }

    public List<Materia> getMaterias() {
        return materias;
    }

    public void setMaterias(List<Materia> materias) {
        this.materias = materias;
    }
}
