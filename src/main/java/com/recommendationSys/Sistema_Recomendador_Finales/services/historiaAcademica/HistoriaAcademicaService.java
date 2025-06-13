package com.recommendationSys.Sistema_Recomendador_Finales.services.historiaAcademica;

import com.recommendationSys.Sistema_Recomendador_Finales.DTOs.HistoriaAcademicaResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface HistoriaAcademicaService {
    HistoriaAcademicaResponseDTO cargarHistoriaAcademica(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException;

    void eliminarHistoriaAcademica(Long estudianteId);

    HistoriaAcademicaResponseDTO actualizarHistoriaAcademica(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException;
}