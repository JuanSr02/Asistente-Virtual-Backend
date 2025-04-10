package model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "PlanDeEstudio")
public class PlanDeEstudio {

    @Id
    @Column(length = 9)
    private String codigo;

    @Column(nullable = false, length = 30)
    private String propuesta;

    @OneToMany(mappedBy = "planDeEstudio", cascade = CascadeType.ALL)
    private List<Materia> materias;

    @OneToMany(mappedBy = "planDeEstudio", cascade = CascadeType.ALL)
    private List<HistoriaAcademica> historiasAcademicas;

    @OneToMany(mappedBy = "planDeEstudio", cascade = CascadeType.ALL)
    private List<Correlativa> correlativas;
}


