package model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "Persona")
@Inheritance(strategy = InheritanceType.JOINED)
public class Persona {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer dni;

    @Column(name = "nombre_apellido", nullable = false, length = 50)
    private String nombreApellido;

    @Column(nullable = false, length = 50)
    private String mail;

    @Column(length = 15)
    private String telefono;

    @Column(unique = true, length = 100)
    private String usuario;

    @Column(nullable = false)
    private String contrasenia;

    @Column(nullable = false, length = 15)
    private String tipo;
}

