package com.recommendationSys.Sistema_Recomendador_Finales.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "persona", uniqueConstraints = {
        @UniqueConstraint(columnNames = "mail"),
        @UniqueConstraint(columnNames = "usuario")
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Persona {

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

    @Size(max = 100, message = "Usuario debe tener máximo 100 caracteres")
    @Column(nullable = false, unique = true, length = 100)
    private String usuario;

    @NotBlank(message = "Contraseña es obligatoria")
    @Column(nullable = false)
    private String contrasenia;

    @NotNull(message = "Tipo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoPersona tipo;

    public enum TipoPersona {
        ESTUDIANTE,
        ADMINISTRADOR
    }
}
