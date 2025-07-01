package com.asistenteVirtual.DTOs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamenDTO {
    private Long id;
    private LocalDate fecha;
    private Double nota;
    private Long renglonId;
    private String materiaCodigo;
    private String materiaNombre;
}