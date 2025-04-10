package model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "Estudiante")
@PrimaryKeyJoinColumn(name = "Persona_id")
public class Estudiante extends Persona {

    @Column(name = "nroRegistro", nullable = false)
    private Integer nroRegistro;

    @OneToOne(mappedBy = "estudiante", cascade = CascadeType.ALL)
    private HistoriaAcademica historiaAcademica;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL)
    private List<RegistroInscripcion> inscripciones;
}