package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "persona", uniqueConstraints = {
        @UniqueConstraint(columnNames = "mail"),
        @UniqueConstraint(columnNames = "usuario")
})
@Inheritance(strategy = InheritanceType.JOINED)
@SuperBuilder
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nombre y apellido es obligatorio")
    @Size(max = 50, message = "Nombre y apellido debe tener máximo 50 caracteres")
    @Column(name = "nombre_apellido", nullable = false, length = 50)
    private String nombreApellido;

    @NotBlank(message = "Email es obligatorio")
    @Email(message = "Email inválido")
    @Size(max = 50, message = "Email debe tener máximo 50 caracteres")
    @Column(nullable = false, unique = true, length = 50)
    private String mail;

    @Size(max = 15, message = "Teléfono debe tener máximo 15 caracteres")
    @Column(length = 15)
    private String telefono;

    private String supabaseUserId;
    }
