package com.asistenteVirtual.services.planEstudio;

import com.asistenteVirtual.DTOs.MateriaDTO;
import com.asistenteVirtual.DTOs.PlanEstudioResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PlanEstudioService {
    PlanEstudioResponseDTO procesarArchivoExcel(MultipartFile file) throws IOException;
    void eliminarPlanDeEstudio(String codigoPlan);
    List<PlanEstudioResponseDTO> obtenerPlanes();
    List<MateriaDTO> obtenerMateriasPorPlan(String codigoPlan);
}