package com.asistenteVirtual.modules.ranking.model;

public enum OrdenFinales {
    CORRELATIVAS, // Ordena por "cuello de botella"
    VENCIMIENTO, // Ordena por urgencia
    ESTADISTICAS, // Ordena por facilidad/probabilidad de aprobación
    COMBINACION_SUPREMA // Combina las 3 estrategías anteriores
}