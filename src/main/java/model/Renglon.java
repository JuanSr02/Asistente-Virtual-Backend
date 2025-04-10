package model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Entity
@Table(name = "Renglon")
public class Renglon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 12)
    private String fecha;

    @Column(nullable = false, length = 50)
    private String tipo;

    @Column(precision = 4, scale = 2)
    private BigDecimal nota;

    @Column(nullable = false, length = 50)
    private String resultado;

    @ManyToOne
    @JoinColumn(name = "historia_id", nullable = false)
    private HistoriaAcademica historiaAcademica;

    @ManyToOne
    @JoinColumn(name = "materia_codigo", nullable = false)
    private Materia materia;

    @OneToOne(mappedBy = "renglon", cascade = CascadeType.ALL)
    private Examen examen;
}


