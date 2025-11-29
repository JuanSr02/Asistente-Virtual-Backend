package com.asistenteVirtual.modules.estadisticas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasMateriaId implements Serializable {
    private String codigoMateria;
    private String periodo;
}