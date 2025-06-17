package com.asistenteVirtual.model;

import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@EqualsAndHashCode
public class CorrelativaId implements Serializable {
    private Materia materia;
    private Materia correlativaCodigo;
}
