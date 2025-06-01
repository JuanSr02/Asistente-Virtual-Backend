package com.recommendationSys.Sistema_Recomendador_Finales.controllers;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.HistoriaAcademicaResponseDTO;
import com.recommendationSys.Sistema_Recomendador_Finales.exceptions.ResourceNotFoundException;
import com.recommendationSys.Sistema_Recomendador_Finales.services.historiaAcademica.HistoriaAcademicaService;
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
@RequestMapping("/api/public/historia-academica/{estudianteId}")
public class HistoriaAcademicaController {

    private final HistoriaAcademicaService historiaAcademicaService;

    /**
     * Carga la historia académica desde un archivo Excel
     * @param file Archivo Excel con los datos
     * @param estudianteId id del estudiante (no puede ser nulo)
     * @return Respuesta con el resultado de la operación
     * @throws ResourceNotFoundException si no encuentra el estudiante
     * @throws IOException si hay error cargando el excel
     */
    @PostMapping("/carga")
    public ResponseEntity<?> cargarHistoriaDesdeExcel(
            @RequestParam("file") @NotNull MultipartFile file,
            @PathVariable @NotNull Long estudianteId) throws IOException {
        log.info("Iniciando carga de historia académica para estudiante ID: {}", estudianteId);
        HistoriaAcademicaResponseDTO response = historiaAcademicaService.cargarHistoriaAcademica(file, estudianteId);
        return ResponseEntity.ok(response);    }
    /**
     * Actualiza la historia académica cargando únicamente renglones nuevos desde Excel
     * @param file Archivo Excel con los datos
     * @param estudianteId ID del estudiante
     * @return DTO con la historia académica actualizada
     * @throws IOException en caso de error leyendo el archivo
     */
    @PatchMapping("/actualizacion")
    public ResponseEntity<HistoriaAcademicaResponseDTO> actualizarHistoriaDesdeExcel(
            @RequestParam("file") @NotNull MultipartFile file,
            @PathVariable @NotNull Long estudianteId) throws IOException {
        log.info("Iniciando actualización de historia académica para estudiante ID: {}", estudianteId);
        HistoriaAcademicaResponseDTO response = historiaAcademicaService.actualizarHistoriaAcademica(file, estudianteId);
        return ResponseEntity.ok(response);
    }

    /**
     * Elimina la historia académica de un estudiante
     * @param estudianteId id del estudiante (no puede ser nulo)
     * @return Respuesta con el resultado de la operación
     * @throws ResourceNotFoundException si no se encuentra la historiaAcademica
     */
    @DeleteMapping
    public ResponseEntity<?> eliminarHistoriaAcademica(
            @PathVariable @NotNull Long estudianteId) {
        log.info("Eliminando historia académica para estudiante ID: {}", estudianteId);
        historiaAcademicaService.eliminarHistoriaAcademica(estudianteId);
        return ResponseEntity.noContent().build();
    }

}