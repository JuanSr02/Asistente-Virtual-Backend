package com.asistenteVirtual.controllers;

import com.asistenteVirtual.DTOs.HistoriaAcademicaResponseDTO;
import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.services.historiaAcademica.HistoriaAcademicaService;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/shared/historia-academica/{estudianteId}")
public class HistoriaAcademicaController {

    private final HistoriaAcademicaService historiaAcademicaService;

    /**
     * Carga la historia académica desde un archivo Excel o PDF
     *
     * @param file         Archivo Excel o PDF con los datos
     * @param estudianteId id del estudiante (no puede ser nulo)
     * @param codigoPlan   codigo del plan al que pertenece la historia
     * @return Respuesta con el resultado de la operación
     * @throws ResourceNotFoundException si no encuentra el estudiante
     * @throws IOException               si hay error cargando el excel o PDF
     */
    @PostMapping("/carga")
    public ResponseEntity<?> cargarHistoriaDesdeExcel(
            @RequestParam("file") @NotNull MultipartFile file,
            @PathVariable @NotNull Long estudianteId, @RequestParam("codigoPlan") @NotBlank String codigoPlan) throws IOException {
        log.warn("Cargando historia");
        HistoriaAcademicaResponseDTO response = historiaAcademicaService.cargarHistoriaAcademica(file, estudianteId, codigoPlan);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza la historia académica cargando únicamente renglones nuevos desde Excel o PDF
     *
     * @param file         Archivo Excel/PDF con los datos
     * @param estudianteId ID del estudiante
     * @param codigoPlan   codigo del plan al que pertenece la historia
     * @return DTO con la historia académica actualizada
     * @throws IOException en caso de error leyendo el archivo
     */
    @PatchMapping("/actualizacion")
    public ResponseEntity<HistoriaAcademicaResponseDTO> actualizarHistoriaDesdeExcel(
            @RequestParam("file") @NotNull MultipartFile file,
            @PathVariable @NotNull Long estudianteId, @RequestParam("codigoPlan") @NotBlank String codigoPlan) throws IOException {
        HistoriaAcademicaResponseDTO response = historiaAcademicaService.actualizarHistoriaAcademica(file, estudianteId, codigoPlan);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina la historia académica de un estudiante
     *
     * @param estudianteId id del estudiante (no puede ser nulo)
     * @return Respuesta con el resultado de la operación
     * @throws ResourceNotFoundException si no se encuentra la historiaAcademica
     */
    @DeleteMapping
    public ResponseEntity<?> eliminarHistoriaAcademica(
            @PathVariable @NotNull Long estudianteId) {
        historiaAcademicaService.eliminarHistoriaAcademica(estudianteId);
        return ResponseEntity.noContent().build();
    }

}