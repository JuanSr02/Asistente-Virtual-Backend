package com.recommendationSys.Sistema_Recomendador_Finales.services.historiaAcademica;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface HistoriaAcademicaService {
    void cargarHistoriaAcademica(MultipartFile file, Long estudianteId) throws IOException;
    void eliminarHistoriaAcademica(Long estudianteId);
}