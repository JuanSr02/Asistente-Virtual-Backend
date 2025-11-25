package com.asistenteVirtual.modules.estadisticas.dto;

public record MateriaRankingResponse(
    String codigoMateria,
    String nombre,
    Double porcentaje
) {}