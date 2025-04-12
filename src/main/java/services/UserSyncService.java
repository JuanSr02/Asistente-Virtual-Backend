package services;

import com.fasterxml.jackson.databind.JsonNode;
import model.Administrador;
import model.Estudiante;
import model.Persona;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import repository.PersonaRepository;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Service
public class UserSyncService {

    @Autowired
    private PersonaRepository personaRepository;

    @Autowired
    private SupabaseAuthService supabaseAuthService;

    /**
     * Sincroniza un usuario de Supabase con nuestra base de datos
     * @param token Token de acceso del usuario
     * @return El usuario sincronizado
     */
    public Mono<Persona> syncUser(String token) {
        return supabaseAuthService.getUserData(token)
                .flatMap(userData -> {
                    String email = userData.get("email").asText();

                    // Buscar si el usuario ya existe en nuestra base de datos
                    Optional<Persona> existingUser = personaRepository.findByMail(email);

                    if (existingUser.isPresent()) {
                        // El usuario ya existe, actualizar datos si es necesario
                        return Mono.just(existingUser.get());
                    } else {
                        // El usuario no existe, crearlo basado en los datos de Supabase
                        return createUserFromSupabaseData(userData);
                    }
                });
    }

    /**
     * Crea un nuevo usuario en nuestra base de datos basado en los datos de Supabase
     * @param userData Datos del usuario de Supabase
     * @return El usuario creado
     */
    private Mono<Persona> createUserFromSupabaseData(JsonNode userData) {
        String email = userData.get("email").asText();
        JsonNode userMetadata = userData.get("user_metadata");

        String tipo = userMetadata.has("tipo") ?
                userMetadata.get("tipo").asText() : "ESTUDIANTE"; // Por defecto es estudiante

        Persona persona;

        if ("ADMINISTRADOR".equals(tipo)) {
            Administrador admin = new Administrador();
            // Establecer valores específicos de administrador
            admin.setToken(1234); // Valor por defecto o desde metadata
            persona = admin;
        } else {
            Estudiante estudiante = new Estudiante();
            // Establecer valores específicos de estudiante
            estudiante.setNroRegistro(1000); // Valor por defecto o desde metadata
            persona = estudiante;
        }

        // Establecer valores comunes
        persona.setMail(email);
        persona.setUsuario(email);
        persona.setTipo(tipo);

        // Establecer otros valores desde metadata si están disponibles
        if (userMetadata.has("nombre_apellido")) {
            persona.setNombreApellido(userMetadata.get("nombre_apellido").asText());
        } else {
            persona.setNombreApellido("Usuario " + email);
        }

        // Guardar en la base de datos
        persona = personaRepository.save(persona);

        return Mono.just(persona);
    }
}

