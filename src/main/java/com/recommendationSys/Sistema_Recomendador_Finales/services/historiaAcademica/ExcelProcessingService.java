package com.recommendationSys.Sistema_Recomendador_Finales.services.historiaAcademica;

import com.recommendationSys.Sistema_Recomendador_Finales.model.HistoriaAcademica;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelProcessingService {
    HistoriaAcademica procesarArchivoExcel(MultipartFile file, Long estudianteId) throws IOException;
    HistoriaAcademica procesarArchivoExcelActualizacion(MultipartFile file, Long estudianteId) throws IOException;
}
