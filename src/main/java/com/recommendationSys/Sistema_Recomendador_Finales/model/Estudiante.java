package com.recommendationSys.Sistema_Recomendador_Finales.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "estudiante")
@PrimaryKeyJoinColumn(name = "persona_id")
@SuperBuilder
public class Estudiante extends Persona {

    @NotNull(message = "El número de registro es obligatorio")
    @Column(name = "nro_registro", unique = true)
    private Integer nroRegistro;

    @OneToOne(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private HistoriaAcademica historiaAcademica;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RegistroInscripcion> inscripciones;
}
