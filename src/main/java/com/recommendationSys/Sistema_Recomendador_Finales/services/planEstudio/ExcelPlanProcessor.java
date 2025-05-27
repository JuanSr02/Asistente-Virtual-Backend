package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelPlanProcessor {
    void procesarContenidoPlan(MultipartFile file) throws IOException;
}
