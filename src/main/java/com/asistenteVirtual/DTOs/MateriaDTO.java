package com.asistenteVirtual.DTOs;

import com.asistenteVirtual.model.Materia;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MateriaDTO {
    private String codigo;
    private String nombre;

    public static MateriaDTO toMateriaDTO (Materia materia){
        return MateriaDTO.builder().codigo(materia.getCodigo()).nombre(materia.getNombre()).build();
    }
}

