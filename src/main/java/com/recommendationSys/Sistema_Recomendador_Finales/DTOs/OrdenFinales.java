package com.recommendationSys.Sistema_Recomendador_Finales.DTOs;

public enum OrdenFinales {
    /** Ordena según cantidad de correlativas */
    CORRELATIVAS,

    /** Ordena según fecha de vencimiento */
    VENCIMIENTO,

    /** Ordena según estadísticas (promedios, % aprobados, etc.) */
    ESTADISTICAS
}
