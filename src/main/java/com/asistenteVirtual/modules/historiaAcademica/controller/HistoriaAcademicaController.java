package com.asistenteVirtual.modules.historiaAcademica.controller;

import com.asistenteVirtual.modules.historiaAcademica.dto.HistoriaAcademicaResponse;
import com.asistenteVirtual.modules.historiaAcademica.service.HistoriaAcademicaService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/shared/historia-academica")
@RequiredArgsConstructor
public class HistoriaAcademicaController {

    private final HistoriaAcademicaService historiaService;

    @PostMapping(value = "/{estudianteId}/carga", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HistoriaAcademicaResponse> cargarHistoria(
            @PathVariable Long estudianteId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("codigoPlan") String codigoPlan) throws IOException {
        
        return ResponseEntity.ok(historiaService.procesarHistoria(file, estudianteId, codigoPlan));
    }

    @PatchMapping(value = "/{estudianteId}/actualizacion", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<HistoriaAcademicaResponse> actualizarHistoria(
            @PathVariable Long estudianteId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("codigoPlan") String codigoPlan) throws IOException {
        
        // Reutilizamos la misma lógica porque nuestro 'ImportService' ya maneja el merge inteligente.
        return ResponseEntity.ok(historiaService.procesarHistoria(file, estudianteId, codigoPlan));
    }

    @GetMapping("/{estudianteId}")
    public ResponseEntity<HistoriaAcademicaResponse> obtenerHistoria(@PathVariable Long estudianteId) {
        return ResponseEntity.ok(historiaService.obtenerHistoria(estudianteId));
    }

    @DeleteMapping("/{estudianteId}")
    public ResponseEntity<Void> eliminarHistoria(@PathVariable Long estudianteId) {
        historiaService.eliminarHistoria(estudianteId);
        return ResponseEntity.noContent().build();
    }
}