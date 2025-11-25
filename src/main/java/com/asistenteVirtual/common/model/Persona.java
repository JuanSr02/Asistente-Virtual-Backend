package com.asistenteVirtual.common.model;

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
@SuperBuilder
@Entity
@Table(name = "persona", uniqueConstraints = {
        @UniqueConstraint(columnNames = "mail"),
        @UniqueConstraint(columnNames = "supabaseUserId")
})
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected Long id;

    @NotBlank
    @Size(max = 50)
    @Column(name = "nombre_apellido", nullable = false, length = 50)
    protected String nombreApellido;

    @NotBlank
    @Email
    @Size(max = 50)
    @Column(nullable = false, unique = true, length = 50)
    protected String mail;

    @Size(max = 15)
    @Column(length = 15)
    protected String telefono;

    @NotBlank
    @Column(nullable = false, unique = true)
    protected String supabaseUserId;

    @NotBlank
    @Column(name = "rol_usuario", nullable = false)
    protected String rolUsuario; // CamelCase para Java estándar
}