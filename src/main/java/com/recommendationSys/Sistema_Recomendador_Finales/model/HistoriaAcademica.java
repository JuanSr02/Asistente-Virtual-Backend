package com.recommendationSys.Sistema_Recomendador_Finales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "HistoriaAcademica")
public class HistoriaAcademica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "Persona_id_estudiante", unique = true)
    private Estudiante estudiante;

    @ManyToOne
    @JoinColumn(name = "PlanDeEstudio_codigo", nullable = false)
    @JsonIgnore
    private PlanDeEstudio planDeEstudio;

    @OneToMany(mappedBy = "historiaAcademica", cascade = CascadeType.ALL,orphanRemoval = true)
    @JsonIgnore
    private List<Renglon> renglones = new ArrayList<>();

    public HistoriaAcademica() {
    }

    public HistoriaAcademica(Long id, Estudiante estudiante, PlanDeEstudio planDeEstudio, List<Renglon> renglones) {
        this.id = id;
        this.estudiante = estudiante;
        this.planDeEstudio = planDeEstudio;
        this.renglones = renglones;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public void setEstudiante(Estudiante estudiante) {
        this.estudiante = estudiante;
    }

    public PlanDeEstudio getPlanDeEstudio() {
        return planDeEstudio;
    }

    public void setPlanDeEstudio(PlanDeEstudio planDeEstudio) {
        this.planDeEstudio = planDeEstudio;
    }

    public List<Renglon> getRenglones() {
        return renglones;
    }

    public void setRenglones(List<Renglon> renglones) {
        this.renglones = renglones;
    }

    @Override
    public String toString() {
        return "HistoriaAcademica{" +
                "id=" + id +
                ", estudiante=" + estudiante +
                ", planDeEstudio=" + planDeEstudio +
                ", renglones=" + renglones +
                '}';
    }
}


