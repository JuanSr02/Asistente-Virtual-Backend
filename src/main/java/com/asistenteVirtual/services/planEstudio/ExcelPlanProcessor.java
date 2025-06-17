package com.asistenteVirtual.services.planEstudio;

import com.asistenteVirtual.model.PlanDeEstudio;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelPlanProcessor {
    PlanDeEstudio procesarContenidoPlan(MultipartFile file) throws IOException;
}
