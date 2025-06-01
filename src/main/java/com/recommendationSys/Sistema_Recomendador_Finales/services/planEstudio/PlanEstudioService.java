package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.PlanEstudioResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PlanEstudioService {
    PlanEstudioResponseDTO procesarArchivoExcel(MultipartFile file) throws IOException;
    void eliminarPlanDeEstudio(String codigoPlan);
}