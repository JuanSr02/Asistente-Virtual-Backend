package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PlanEstudioService {
    void procesarArchivoExcel(MultipartFile file) throws IOException;
    void eliminarPlanDeEstudio(String codigoPlan);
}