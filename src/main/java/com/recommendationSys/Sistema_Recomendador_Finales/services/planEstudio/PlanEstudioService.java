package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.PlanEstudioResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface PlanEstudioService {
    PlanEstudioResponseDTO procesarArchivoExcel(MultipartFile file) throws IOException;
    void eliminarPlanDeEstudio(String codigoPlan);
    List<PlanEstudioResponseDTO> obtenerPlanes();
}