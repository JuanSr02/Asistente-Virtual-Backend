package com.asistenteVirtual.modules.security.service;

import com.asistenteVirtual.common.exceptions.AccesoDenegadoException;
import com.asistenteVirtual.common.exceptions.ResourceNotFoundException;
import com.asistenteVirtual.common.repository.PersonaRepository;
import com.asistenteVirtual.modules.estudiante.repository.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityValidator {

    private final EstudianteRepository estudianteRepository;
    private final PersonaRepository personaRepository;

    // Define la constante una sola vez para evitar "magic strings"
    private static final String ROLE_ADMIN = "ROLE_ADMINISTRADOR";

    public void validarAccesoEstudiante(Long estudianteId) {
        var estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new ResourceNotFoundException("Estudiante no encontrado"));

        // Reutilizamos la lógica centralizada
        validarQueSeaDuenioOAdmin(estudiante.getSupabaseUserId());
    }

    public void validarAccesoPersonaSupabase(String supabaseUserId) {
        var persona = personaRepository.findBySupabaseUserId(supabaseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada"));

        validarQueSeaDuenioOAdmin(persona.getSupabaseUserId());
    }

    public void validarAccesoPersonaMail(String mail) {
        var persona = personaRepository.findByMail(mail)
                .orElseThrow(() -> new ResourceNotFoundException("Persona no encontrada"));

        validarQueSeaDuenioOAdmin(persona.getSupabaseUserId());
    }
    
    /**
     * Centraliza la lógica de seguridad:
     * 1. Obtiene el usuario del contexto.
     * 2. Si es ADMIN, permite todo.
     * 3. Si no es ADMIN, valida que el ID del token coincida con el del recurso.
     */
    private void validarQueSeaDuenioOAdmin(String idDuenioRecurso) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Validar Admin (Fast exit)
        boolean esAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(ROLE_ADMIN));

        if (esAdmin) return;

        // Validar Dueño
        String usuarioActualId = (String) auth.getPrincipal();

        if (!idDuenioRecurso.equals(usuarioActualId)) {
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