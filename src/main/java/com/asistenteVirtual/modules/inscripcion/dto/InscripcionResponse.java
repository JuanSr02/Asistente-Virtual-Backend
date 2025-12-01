package com.asistenteVirtual.modules.inscripcion.dto;

import com.asistenteVirtual.modules.inscripcion.model.Inscripcion;

public record InscripcionResponse(
        Long id,
        String turno,
        Integer anio,
        String materiaCodigo,
        String materiaNombre,
        String materiaPlan,
        Long estudianteId,
        String estudianteNombre
) {
    public static InscripcionResponse fromEntity(Inscripcion i) {
        return new InscripcionResponse(
                i.getId(),
                i.getTurno(),
                i.getAnio(),
                i.getMateria().getCodigo(),
                i.getMateria().getNombre(),
                i.getMateria().getPlanDeEstudio().getCodigo(), // Ojo: esto requiere fetch del plan si es Lazy
                i.getEstudiante().getId(),
                i.getEstudiante().getNombreApellido()
        );
    }
}