package com.asistenteVirtual.services.historiaAcademica;

import com.asistenteVirtual.DTOs.HistoriaAcademicaResponseDTO;
import com.asistenteVirtual.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.exceptions.UnsupportedFileTypeException;
import com.asistenteVirtual.model.Estudiante;
import com.asistenteVirtual.model.HistoriaAcademica;
import com.asistenteVirtual.repository.EstudianteRepository;
import com.asistenteVirtual.repository.HistoriaAcademicaRepository;
import com.asistenteVirtual.repository.RenglonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class HistoriaAcademicaServiceImpl implements HistoriaAcademicaService {

    private final HistoriaAcademicaRepository historiaRepo;
    private final RenglonRepository renglonRepo;
    private final EstudianteRepository estudianteRepo;
    private final ArchivoProcessingService archivoProcessingService;
    private final HistoriaAcademicaValidator validator;

    @Override
    public HistoriaAcademicaResponseDTO cargarHistoriaAcademica(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        validator.validarEstudiante(estudianteId);
        boolean tieneHistoria = validator.validarHistoria(estudianteId);
        if (tieneHistoria) {
            return actualizarHistoriaAcademica(file, estudianteId, codigoPlan);
        } else {
            HistoriaAcademica historia;
            String fileExtension = getFileExtension(file);

            switch (fileExtension) {
                case "xlsx":
                case "xls":
                    historia = archivoProcessingService.procesarArchivoExcel(file, estudianteId, codigoPlan);
                    break;
                case "pdf":
                    historia = archivoProcessingService.procesarArchivoPDF(file, estudianteId, codigoPlan);
                    break;
                default:
                    throw new UnsupportedFileTypeException("Tipo de archivo no soportado: ." + fileExtension + ". Solo se permiten archivos .xlsx, .xls y .pdf.");
            }
            Long renglonesCargados = renglonRepo.countByHistoriaAcademica(historia);
            return HistoriaAcademicaResponseDTO.builder()
                    .nombreCompleto(historia.getEstudiante().getNombreApellido())
                    .codigoPlan(historia.getPlanDeEstudio().getCodigo())
                    .fechaUltimaActualizacion(LocalDate.now())
                    .renglonesCargados(renglonesCargados)
                    .build();
        }
    }

    @Override
    public HistoriaAcademicaResponseDTO actualizarHistoriaAcademica(MultipartFile file, Long estudianteId, String codigoPlan) throws IOException {
        validator.validarEstudiante(estudianteId);
        // Obtener la historia académica existente para contar los renglones originales
        HistoriaAcademica historiaExistente = historiaRepo.findByEstudiante(estudianteRepo.findById(estudianteId)
                        .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado con ID: " + estudianteId)))
                .orElseThrow(() -> new ResourceNotFoundException("Historia académica no encontrada para el estudiante con ID: " + estudianteId));


        HistoriaAcademica historiaActualizada;
        String fileExtension = getFileExtension(file);

        switch (fileExtension) {
            case "xlsx":
            case "xls":
                historiaActualizada = archivoProcessingService.procesarArchivoExcelActualizacion(file, estudianteId, codigoPlan);
                break;
            case "pdf":
                historiaActualizada = archivoProcessingService.procesarArchivoPDFActualizacion(file, estudianteId, codigoPlan);
                break;
            default:
                throw new UnsupportedFileTypeException("Tipo de archivo no soportado: ." + fileExtension + ". Solo se permiten archivos .xlsx, .xls y .pdf.");
        }


        return HistoriaAcademicaResponseDTO.builder()
                .nombreCompleto(historiaActualizada.getEstudiante().getNombreApellido())
                .codigoPlan(historiaActualizada.getPlanDeEstudio().getCodigo())
                .fechaUltimaActualizacion(LocalDate.now())
                .build();
    }


    @Override
    public void eliminarHistoriaAcademica(Long estudianteId) {
        Estudiante estudiante = estudianteRepo.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        HistoriaAcademica historia = estudiante.getHistoriaAcademica();
        if (historia != null) {
            historia.setEstado("BAJA");
            historiaRepo.save(historia);
            historiaRepo.flush();
        }
    }

    /**
     * Metodo auxiliar para obtener la extensión del archivo.
     *
     * @param file El MultipartFile.
     * @return La extensión del archivo en minúsculas, o una cadena vacía si no tiene extensión.
     */
    private String getFileExtension(MultipartFile file) {
        String fileName = file.getOriginalFilename();
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        }
        return "";
    }
}