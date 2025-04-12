package config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Configuration
public class EnvLoader {



    @PostConstruct
    public void loadEnv() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .directory("src/main/resources")
                    .filename(".env")
                    .load();

            // Cargar variables al sistema
            System.setProperty("SUPABASE_URL", dotenv.get("SUPABASE_URL"));
            System.setProperty("SUPABASE_ANON_KEY", dotenv.get("SUPABASE_ANON_KEY"));
            System.out.println("SUPABASE_URL cargada: " + System.getProperty("SUPABASE_URL"));

            // Variables de base de datos
            System.setProperty("SUPABASE_DB_HOST", dotenv.get("SUPABASE_DB_HOST"));
            System.setProperty("SUPABASE_DB_PORT", dotenv.get("SUPABASE_DB_PORT"));
            System.setProperty("SUPABASE_DB_NAME", dotenv.get("SUPABASE_DB_NAME"));
            System.setProperty("SUPABASE_DB_USER", dotenv.get("SUPABASE_DB_USER"));
            System.setProperty("SUPABASE_DB_PASSWORD", dotenv.get("SUPABASE_DB_PASSWORD"));
        } catch (Exception e) {
            System.err.println("Error al cargar variables de entorno: " + e.getMessage());
        }
    }
}

