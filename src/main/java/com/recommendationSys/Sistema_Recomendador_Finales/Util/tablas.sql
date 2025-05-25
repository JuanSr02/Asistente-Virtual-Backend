-- TABLA PERSONA
CREATE TABLE Persona (
    id SERIAL PRIMARY KEY,
    nombre_apellido VARCHAR(50) NOT NULL,
    mail VARCHAR(50) NOT NULL UNIQUE,
    telefono VARCHAR(15),
    usuario VARCHAR(100) UNIQUE,
    contrasenia VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TABLA ESTUDIANTE
CREATE TABLE Estudiante (
    nroRegistro INT UNIQUE,
    Persona_id INT PRIMARY KEY,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (Persona_id) REFERENCES Persona(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA ADMINISTRADOR
CREATE TABLE Administrador (
    Persona_id INT PRIMARY KEY,
    token INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (Persona_id) REFERENCES Persona(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA PLAN DE ESTUDIO
CREATE TABLE PlanDeEstudio (
    codigo VARCHAR(9) PRIMARY KEY,
    propuesta VARCHAR(30) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- TABLA MATERIA
CREATE TABLE Materia (
    codigo VARCHAR(15) PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    PlanDeEstudio_codigo VARCHAR(9),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (PlanDeEstudio_codigo) REFERENCES PlanDeEstudio(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA CORRELATIVA
CREATE TABLE Correlativa (
    materia_codigo VARCHAR(15),
    correlativa_codigo VARCHAR(15),
    PlanDeEstudio_codigo VARCHAR(9),
    PRIMARY KEY (materia_codigo, correlativa_codigo, PlanDeEstudio_codigo),
    FOREIGN KEY (PlanDeEstudio_codigo) REFERENCES PlanDeEstudio(codigo) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (materia_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (correlativa_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA HISTORIA ACADEMICA
CREATE TABLE HistoriaAcademica (
    id SERIAL PRIMARY KEY,
    Persona_id_estudiante INT UNIQUE,
    PlanDeEstudio_codigo VARCHAR(9) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (Persona_id_estudiante) REFERENCES Estudiante(Persona_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (PlanDeEstudio_codigo) REFERENCES PlanDeEstudio(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA RENGLÓN
CREATE TABLE Renglon (
    id SERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    nota DECIMAL(4,2),
    resultado VARCHAR(50) NOT NULL,
    historia_id INT NOT NULL,
    materia_codigo VARCHAR(15) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (historia_id) REFERENCES HistoriaAcademica(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (materia_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA EXAMEN
CREATE TABLE Examen (
    id SERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    nota DECIMAL(4,2),
    Renglon_id INT UNIQUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (Renglon_id) REFERENCES Renglon(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA REGISTRO INSCRIPCIÓN
CREATE TABLE RegistroInscripcion (
    id SERIAL PRIMARY KEY,
    turno VARCHAR(50),
    anio INT NOT NULL,
    materia_codigo VARCHAR(15) NOT NULL,
    Persona_id_estudiante INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (Persona_id_estudiante) REFERENCES Estudiante(Persona_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (materia_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

-- TABLA EXPERIENCIA
CREATE TABLE Experiencia (
    id SERIAL PRIMARY KEY,
    examen_id INT UNIQUE,
    dificultad INT NOT NULL,
    dias_estudio INT NOT NULL,
    horas_diarias INT NOT NULL,
    intentos_previos INT NOT NULL,
    modalidad VARCHAR(20),
    recursos VARCHAR(200),
    motivacion VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (examen_id) REFERENCES Examen(id) ON DELETE CASCADE ON UPDATE CASCADE
);

-- ÍNDICES RECOMENDADOS
CREATE INDEX idx_renglon_historia_id ON Renglon(historia_id);
CREATE INDEX idx_renglon_materia_codigo ON Renglon(materia_codigo);
CREATE INDEX idx_experiencia_examen_id ON Experiencia(examen_id);
