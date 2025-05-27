package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "registro_inscripcion")
@Builder
public class RegistroInscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(max = 50, message = "El turno no puede superar los 50 caracteres")
    @Column(length = 50)
    private String turno;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2012, message = "El año debe ser mayor o igual a 2012")
    @Max(value = 2028, message = "El año no puede ser mayor o igual a 2028")
    @Column(nullable = false)
    private Integer anio;

    @NotNull(message = "La materia es obligatoria")
    @ManyToOne
    @JoinColumn(name = "materia_codigo", nullable = false)
    private Materia materia;

    @NotNull(message = "El estudiante es obligatorio")
    @ManyToOne
    @JoinColumn(name = "persona_id_estudiante", nullable = false)
    private Estudiante estudiante;
}
