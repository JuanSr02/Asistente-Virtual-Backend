package com.asistenteVirtual.services.historiaAcademica;

import com.asistenteVirtual.model.HistoriaAcademica;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ExcelProcessingService {
    HistoriaAcademica procesarArchivoExcel(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException;

    HistoriaAcademica procesarArchivoExcelActualizacion(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException;
}
