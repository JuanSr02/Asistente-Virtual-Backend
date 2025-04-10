package model;

import jakarta.persistence.*;
import lombok.Data;

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
    private PlanDeEstudio planDeEstudio;

    @OneToMany(mappedBy = "historiaAcademica", cascade = CascadeType.ALL)
    private List<Renglon> renglones;
}


