# 🎓 Asistente Virtual - Backend

![Java](https://img.shields.io/badge/Java-21-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.4.3-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)

Backend API RESTful para la plataforma "Asistente Virtual", diseñado para optimizar la vida académica de estudiantes universitarios. Permite gestionar historias académicas, recibir recomendaciones de estudio personalizadas, conectar con compañeros y visualizar estadísticas detalladas.

## 🚀 Características Principales

* **Gestión de Historia Académica**: Procesamiento automático de archivos **PDF** y **Excel** para importar materias aprobadas, regularidades y promedios.
* **Motor de Recomendaciones (Ranking)**: Algoritmo que sugiere qué finales rendir priorizando por correlativas ("cuello de botella"), vencimiento de regularidades o probabilidad estadística de aprobación.
* **Networking Académico**: Sistema de inscripción a mesas de examen que notifica automáticamente por **Email** cuando otros compañeros se anotan en la misma fecha y turno.
* **Estadísticas Avanzadas**: 
    * Cálculo de tasas de aprobación, dificultad percibida y tiempos de estudio por materia.
    * Sistema de **caché** con estrategias de actualización rápida (`FastStatisticsService`) vs. cálculo intensivo en segundo plano.
* **Experiencias y Feedback**: Módulo para que los estudiantes compartan reseñas, dificultad y recursos sobre los exámenes rendidos.
* **Seguridad y Roles**: Autenticación delegada en **Supabase** (JWT) con control de acceso basado en roles (`ADMINISTRADOR`, `ESTUDIANTE`).

## 🛠️ Stack Tecnológico

* **Lenguaje**: Java 21 (LTS).
* **Framework**: Spring Boot 3.4.3.
* **Base de Datos**: PostgreSQL.
* **Autenticación**: Supabase Auth (JWT).
* **Procesamiento de Archivos**: Apache POI (Excel) y PDFBox (PDF).
* **Emails**: JavaMailSender (SMTP Gmail).
* **Despliegue**: Docker & Docker Compose.

---

## ⚙️ Guía de Instalación y Ejecución

### Prerrequisitos
* Java 21 JDK
* Maven
* Docker (opcional, para base de datos local)

### 1. Configuración de Variables de Entorno
El sistema utiliza perfiles de Spring. Para producción (o ejecución completa), configura las siguientes variables (basado en `application-prod.properties`):

| Variable | Descripción | Ejemplo |
|----------|-------------|---------|
| `DB_URL` | URL de conexión JDBC | `jdbc:postgresql://localhost:5432/mydatabase` |
| `DB_USER` | Usuario de la BD | `myuser` |
| `DB_PASS` | Password de la BD | `secret` |
| `SUPABASE_URL` | URL del proyecto Supabase | `https://xyz.supabase.co` |
| `SUPABASE_JWT_SECRET` | Secret para validar tokens | `eyJh...` |
| `SUPABASE_SERVICE_ROLE_KEY` | Key para administración de usuarios | `eyJh...` |
| `MAIL_USER` | Gmail para envío de notificaciones | `tu-email@gmail.com` |
| `MAIL_PASS` | Contraseña de aplicación de Google | `abcd efgh ijkl mnop` |

### 2. Ejecutar con Docker Compose (Base de Datos)
Si no tienes PostgreSQL instalado, levanta el contenedor incluido:

docker-compose up -d

### 3. Ejecutar la aplicación
Usando el wrapper de Maven incluido:

# Linux/Mac
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# Windows
.\mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=prod

## 📡 Documentación de API

La API requiere el header `Authorization: Bearer <TOKEN>` para endpoints protegidos.

### 🧑‍🎓 Estudiantes (`/api/shared/estudiantes`)
* **POST** `/api/public/estudiantes`: Registrar nuevo estudiante.
* **GET** `/:id`: Obtener perfil.
* **PATCH** `/:id`: Actualizar datos (Email, Teléfono).

### 📚 Historia Académica (`/api/shared/historia-academica`)
* **POST** `/:estudianteId/carga`: Subir archivo (`.pdf` o `.xlsx`) para procesar historia.
    * *Params*: `file` (Multipart), `codigoPlan` (String).
* **GET** `/:estudianteId`: Consultar estado académico actual.

### 🏆 Ranking y Recomendaciones (`/api/shared/finales`)
* **GET** `/:estudianteId`: Obtener lista de finales sugeridos.
    * *Query Param*: `orden` (`CORRELATIVAS`, `VENCIMIENTO`, `ESTADISTICAS`).
* **GET** `/:estudianteId/inscripciones`: Materias habilitadas para inscripción.

### 📝 Inscripciones (`/api/shared/inscripciones`)
* **POST** `/:`: Inscribirse a una mesa. Dispara emails a compañeros.
* **GET** `/:`: Listar compañeros en una materia/turno/año.

### 📊 Estadísticas (`/api/shared/estadisticas`)
* **GET** `/generales`: Métricas globales del sistema.
* **GET** `/materia/{codigoMateria}`: Métricas de aprobación y dificultad por materia.
* **GET** `/generales/carrera`: Estadísticas filtradas por plan de estudio.

### 🗣️ Experiencias (`/api/shared/experiencias`)
* **POST** `/:`: Cargar reseña de examen (dificultad, tiempo estudio, recursos).
* **GET** `/por-materia/{codigo}`: Ver experiencias de otros alumnos.

### 🛠️ Administración (`/api/admin`) - *Requiere Rol ADMIN*
* **POST** `/planes-estudio/carga`: Carga masiva de planes vía Excel.
* **POST** `/administradores`: Crear nuevos administradores.

---

## 🏗️ Arquitectura del Proyecto

El proyecto sigue una estructura modular vertical (Package by Feature):

* **modules/historiaAcademica**: Lógica de *Strategy Pattern* para parsers (`PdfHistoriaParser`, `ExcelHistoriaParser`) y reglas de negocio para validar planes y regularidades.
* **modules/estadisticas**: Separación de responsabilidades entre cálculo pesado (`EstadisticasService`) y lectura rápida (`FastStatisticsService`).
* **modules/ranking**: Implementación de estrategias de ordenamiento para recomendaciones (`RankingStrategy`).
* **modules/security**: Filtro `SupabaseJwtAuthFilter` para integración transparente con Supabase.

## 📄 Licencia
Este proyecto es software propietario/privado.
