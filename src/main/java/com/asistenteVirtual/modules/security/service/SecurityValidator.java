package com.asistenteVirtual.modules.security.service;

import com.asistenteVirtual.common.exceptions.AccesoDenegadoException;
import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.modules.estudiante.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityValidator {

    private final EstudianteRepository estudianteRepository;

    /**
     * Valida que el usuario logueado sea el dueño del ID de estudiante proporcionado.
     * Permite acceso si el usuario es ADMINISTRADOR.
     */
    public void validarAccesoEstudiante(Long estudianteId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActualSupabaseId = (String) auth.getPrincipal();
        
        // 1. Si es ADMIN, pase libre (opcional, según tu regla de negocio)
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
        if (esAdmin) return;

        // 2. Buscamos al estudiante dueño del recurso
        var estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado (Validación de seguridad)"));

        // 3. Comparamos el ID de Supabase del estudiante con el del Token
        if (!estudiante.getSupabaseUserId().equals(usuarioActualSupabaseId)) {
            throw new AccesoDenegadoException("No tienes permiso para acceder a los datos de otro estudiante.");
        }
    }

    /**
     * Sobrecarga para validar directamente contra un SupabaseID si ya tienes la entidad cargada.
     */
    public void validarAutoria(String propietarioSupabaseId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String usuarioActualId = (String) auth.getPrincipal();
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));

        if (!esAdmin && !propietarioSupabaseId.equals(usuarioActualId)) {
            throw new AccesoDenegadoException("Operación no permitida sobre recursos ajenos.");
        }
    }
}