package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

import com.recommendationSys.Sistema_Recomendador_Finales.model.Materia;
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

