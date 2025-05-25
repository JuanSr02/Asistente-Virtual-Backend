package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "administrador")
@PrimaryKeyJoinColumn(name = "persona_id")
public class Administrador extends Persona {

    @NotNull(message = "El token es obligatorio")
    @Column(nullable = false)
    private Integer token;
}
