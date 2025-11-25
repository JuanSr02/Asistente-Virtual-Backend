package com.asistenteVirtual.modules.experiencia.dto;

import com.asistenteVirtual.modules.experiencia.model.Experiencia;

import java.time.LocalDate;

public record ExperienciaResponse(
        Long id,
        Integer dificultad,
        Integer diasEstudio,
        Integer horasDiarias,
        Integer intentosPrevios,
        String modalidad,
        String recursos,
        String motivacion,
        String linkResumen,
        // Datos aplanados para facilitar consumo en Frontend
        Long examenId,
        LocalDate fechaExamen,
        Double nota,
        String codigoMateria,
        String nombreMateria,
        String nombreEstudiante
) {
    // Mapper estático para mantener la lógica de transformación cerca de los datos
    public static ExperienciaResponse fromEntity(Experiencia e) {
        var examen = e.getExamen();
        var renglon = examen.getRenglon();
        var materia = renglon.getMateria();
        var estudiante = renglon.getHistoriaAcademica().getEstudiante();

        return new ExperienciaResponse(
                e.getId(),
                e.getDificultad(),
                e.getDiasEstudio(),
                e.getHorasDiarias(),
                e.getIntentosPrevios(),
                e.getModalidad(),
                e.getRecursos(),
                e.getMotivacion(),
                e.getLinkResumen(),
                examen.getId(),
                examen.getFecha(),
                examen.getNota(),
                materia.getCodigo(),
                materia.getNombre(),
                estudiante.getNombreApellido()
        );
    }
}