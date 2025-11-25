package com.asistenteVirtual.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Examen")
public class Examen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate fecha;

    private Double nota;

    @OneToOne
    @JoinColumn(name = "Renglon_id", unique = true)
    @JsonIgnore
    private Renglon renglon;

    @OneToOne(mappedBy = "examen", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Experiencia experiencia;

    public Examen(LocalDate fecha, Double nota, Renglon renglon) {
        this.fecha = fecha;
        this.nota = nota;
        this.renglon = renglon;
    }
}
