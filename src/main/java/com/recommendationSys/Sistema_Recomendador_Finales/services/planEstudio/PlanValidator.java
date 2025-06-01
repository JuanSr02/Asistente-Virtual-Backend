package com.recommendationSys.Sistema_Recomendador_Finales.services.planEstudio;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.web.multipart.MultipartFile;

public interface PlanValidator {
    void validarArchivo(MultipartFile file);
    void validarFilaPlan(Row planRow);
    void validarFilaMateria(Row row, int rowNum);
    void validarPlan(String codigoPlan);
}
