package com.asistenteVirtual.model;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class MateriaId implements Serializable {
    private String codigo;
    private PlanDeEstudio planDeEstudio;
}