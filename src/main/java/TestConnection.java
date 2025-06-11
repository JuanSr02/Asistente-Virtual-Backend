import java.sql.Connection;
import java.sql.DriverManager;

public class TestConnection {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://aws-0-sa-east-1.pooler.supabase.com:5432/postgres";
        String user = "postgres.qlbpcnyjsvhxnncjorku";
        String password = "unsl2025";

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            System.out.println("✅ Conexión exitosa!");
        } catch (Exception e) {
            System.out.println("❌ Error de conexión: " + e.getMessage());
            e.printStackTrace();
        }
    }
}