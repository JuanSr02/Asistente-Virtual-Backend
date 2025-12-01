package com.asistenteVirtual.modules.planEstudio.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MateriaId implements Serializable {
    private String codigo;
    private String planDeEstudio; // Debe coincidir con el nombre del atributo en la entidad Materia
}