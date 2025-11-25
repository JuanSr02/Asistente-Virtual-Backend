package com.asistenteVirtual.modules.historiaAcademica.service.parser;

import org.springframework.web.multipart.MultipartFile;

import com.asistenteVirtual.modules.historiaAcademica.dto.DatosFila;

import java.io.IOException;
import java.util.List;

public interface HistoriaFileParser {
    /**
     * Parsea el archivo y devuelve una lista de datos normalizados.
     * @param file Archivo subido por el usuario.
     * @return Lista de registros crudos encontrados.
     * @throws IOException Si hay error de lectura.
     */
    List<DatosFila> parse(MultipartFile file) throws IOException;
}