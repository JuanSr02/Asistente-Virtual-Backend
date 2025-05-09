package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegistroInscripcionDTO {
    @NotBlank(message = "El turno es obligatorio")
    @Size(max = 50, message = "El turno no puede exceder los 50 caracteres")
    private String turno;

    @NotNull(message = "El año es obligatorio")
    @Min(value = 2000, message = "El año debe ser mayor o igual a 2000")
    private Integer anio;

    @NotBlank(message = "El código de materia es obligatorio")
    private String materiaCodigo;

    @NotNull(message = "El ID de estudiante es obligatorio")
    private Long estudianteId;

    // Getters y Setters

    public RegistroInscripcionDTO() {
    }

    public RegistroInscripcionDTO(String turno, Integer anio, String materiaCodigo, Long estudianteId) {
        this.turno = turno;
        this.anio = anio;
        this.materiaCodigo = materiaCodigo;
        this.estudianteId = estudianteId;
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

    public String getMateriaCodigo() {
        return materiaCodigo;
    }

    public void setMateriaCodigo(String materiaCodigo) {
        this.materiaCodigo = materiaCodigo;
    }

    public Long getEstudianteId() {
        return estudianteId;
    }

    public void setEstudianteId(Long estudianteId) {
        this.estudianteId = estudianteId;
    }
}
