package com.asistenteVirtual.modules.planEstudio.controller;

import com.asistenteVirtual.modules.planEstudio.dto.MateriaBusquedaRequest;
import com.asistenteVirtual.modules.planEstudio.dto.MateriaResponse;
import com.asistenteVirtual.modules.planEstudio.service.PlanEstudioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/shared/materias")
@RequiredArgsConstructor
public class MateriaController {

    private final PlanEstudioService planEstudioService;

    @PostMapping("/buscar-lista")
    public ResponseEntity<List<MateriaResponse>> buscarMateriasPorLista(
            @RequestBody List<MateriaBusquedaRequest> materiasBuscadas) {
        return ResponseEntity.ok(planEstudioService.buscarMateriasPorCodigos(materiasBuscadas));
    }
}