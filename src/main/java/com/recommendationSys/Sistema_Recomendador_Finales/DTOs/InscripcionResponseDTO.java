package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;
public class InscripcionResponseDTO {
    private Long id;
    private String turno;
    private Integer anio;
    private String materiaNombre;
    private String materiaCodigo;
    private String estudianteNombre;
    private Long estudianteId;
    private Integer estudianteNroRegistro;

    // Getters y Setters

    public InscripcionResponseDTO() {
    }

    public InscripcionResponseDTO(Long id, String turno, Integer anio, String materiaNombre, String materiaCodigo, String estudianteNombre, Long estudianteId, Integer estudianteNroRegistro) {
        this.id = id;
        this.turno = turno;
        this.anio = anio;
        this.materiaNombre = materiaNombre;
        this.materiaCodigo = materiaCodigo;
        this.estudianteNombre = estudianteNombre;
        this.estudianteId = estudianteId;
        this.estudianteNroRegistro = estudianteNroRegistro;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getMateriaNombre() {
        return materiaNombre;
    }

    public void setMateriaNombre(String materiaNombre) {
        this.materiaNombre = materiaNombre;
    }

    public String getMateriaCodigo() {
        return materiaCodigo;
    }

    public void setMateriaCodigo(String materiaCodigo) {
        this.materiaCodigo = materiaCodigo;
    }

    public String getEstudianteNombre() {
        return estudianteNombre;
    }

    public void setEstudianteNombre(String estudianteNombre) {
        this.estudianteNombre = estudianteNombre;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }

    public Integer getEstudianteNroRegistro() {
        return estudianteNroRegistro;
    }

    public void setEstudianteNroRegistro(Integer estudianteNroRegistro) {
        this.estudianteNroRegistro = estudianteNroRegistro;
    }
}
