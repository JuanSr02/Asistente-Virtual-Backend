# 🎓 Asistente Virtual - Backend

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.3-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)

Backend API RESTful para la plataforma "Asistente Virtual", diseñado para ayudar a estudiantes universitarios a gestionar su vida académica, encontrar compañeros de estudio, visualizar estadísticas avanzadas y recibir recomendaciones sobre qué materias rendir.

## 🚀 Características Principales

* **Gestión de Historia Académica**: 
    * Importación automática mediante **parsing de PDF y Excel** (Strategy Pattern).
    * Detección inteligente de materias aprobadas, regulares y promocionadas.
* **Motor de Recomendaciones (Ranking)**: 
    * Algoritmo que sugiere qué finales rendir basado en correlativas, fechas de vencimiento de regularidades y probabilidad estadística de aprobación.
* **Inscripciones y Networking**: 
    * Sistema para inscribirse a mesas de examen y encontrar automáticamente compañeros (con notificaciones por Email).
* **Estadísticas Avanzadas**: 
    * Cálculo de dificultad, tiempo de estudio promedio y tasas de aprobación por materia.
    * Sistema de **Caché** con actualización programada (Cron Jobs) para alto rendimiento.
* **Experiencias**: 
    * Módulo de feedback donde los estudiantes comparten recursos, consejos y dificultad de los exámenes rendidos.
* **Seguridad**: 
    * Integración con **Supabase Auth** mediante JWT.
    * Control de acceso basado en roles (RBAC: `ADMINISTRADOR`, `ESTUDIANTE`).

## 🛠️ Stack Tecnológico

* **Lenguaje**: Java 21 (LTS)
* **Framework**: Spring Boot 3.4.3
* **Base de Datos**: PostgreSQL
* **Autenticación**: Supabase (JWT)
* **Procesamiento de Archivos**: Apache POI (Excel), PDFBox (PDF)
* **Emails**: JavaMailSender (SMTP Gmail)
* **Infraestructura**: Docker & Docker Compose

## ⚙️ Instalación y Ejecución

### Prerrequisitos
* Java 21 JDK
* Maven
* Docker (Opcional, para la BD)

### 1. Clonar el repositorio
git clone [https://github.com/juansr02/asistente-virtual-backend.git](https://github.com/juansr02/asistente-virtual-backend.git)
cd asistente-virtual-backend

2. Configurar Variables de Entorno
Crea tus variables de entorno (ver archivo ENV_VARS.md para más detalles) o configura tu IDE con lo siguiente:

DB_URL=jdbc:postgresql://localhost:5432/mydatabase
DB_USER=myuser
DB_PASS=secret
SUPABASE_URL=[https://tu-proyecto.supabase.co](https://tu-proyecto.supabase.co)
SUPABASE_JWT_SECRET=tu_jwt_secret
SUPABASE_SERVICE_ROLE_KEY=tu_service_role_key
MAIL_USER=tu_email@gmail.com
MAIL_PASS=tu_app_password

3. Levantar Base de Datos (Docker)
Si no tienes Postgres instalado localmente, usa el archivo compose incluido:

docker-compose up -d

4. Ejecutar la aplicación
./mvnw spring-boot:run

La API estará disponible en http://localhost:8080.

🏗️ Arquitectura del Proyecto
El proyecto sigue una arquitectura modular por funcionalidad (Package by Feature) para facilitar la escalabilidad:

modules/admin: Gestión de administradores.

modules/estudiante: Gestión de perfiles de alumnos.

modules/historiaAcademica: Parsers y lógica de importación.

modules/estadisticas: Servicios de cálculo intensivo y jobs programados.

modules/ranking: Lógica de negocio para priorización de exámenes.

modules/security: Filtros JWT y configuración de Spring Security.

🤝 Contribución
¡Las contribuciones son bienvenidas! Por favor, abre un issue primero para discutir lo que te gustaría cambiar.

Fork el proyecto

Crea tu rama (git checkout -b feature/AmazingFeature)

Commit tus cambios (git commit -m 'Add some AmazingFeature')

Push a la rama (git push origin feature/AmazingFeature)

Abre un Pull Request

📄 Licencia
Distribuido bajo la licencia MIT. Ver LICENSE para más información.
