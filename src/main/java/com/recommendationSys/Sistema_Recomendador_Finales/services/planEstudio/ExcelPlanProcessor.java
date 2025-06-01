package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import com.recommendationSys.Sistema_Recomendador_Finales.model.PlanDeEstudio;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelPlanProcessor {
    PlanDeEstudio procesarContenidoPlan(MultipartFile file) throws IOException;
}
