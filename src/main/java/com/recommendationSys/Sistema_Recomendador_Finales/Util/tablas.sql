CREATE TABLE Persona (
    id SERIAL PRIMARY KEY,
    dni INT NOT NULL,
    nombre_apellido VARCHAR(50) NOT NULL,
    mail VARCHAR(50) NOT NULL,
    telefono VARCHAR(15),
    usuario VARCHAR(100) UNIQUE,
    contrasenia VARCHAR(255) NOT NULL,
    tipo VARCHAR(15) NOT NULL
);

CREATE TABLE Estudiante (
    nroRegistro INT NOT NULL,
    Persona_id INT PRIMARY KEY,
    FOREIGN KEY (Persona_id) REFERENCES Persona(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Administrador (
    Persona_id INT PRIMARY KEY,
    token INT NOT NULL,
    FOREIGN KEY (Persona_id) REFERENCES Persona(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE PlanDeEstudio (
    codigo VARCHAR(9) PRIMARY KEY,
    propuesta VARCHAR(30) NOT NULL
);

CREATE TABLE Materia (
    codigo VARCHAR(15) PRIMARY KEY,
    nombre VARCHAR(200) NOT NULL,
    PlanDeEstudio_codigo VARCHAR(9),
    FOREIGN KEY (PlanDeEstudio_codigo) REFERENCES PlanDeEstudio(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Correlativa (
    materia_codigo VARCHAR(15),
    correlativa_codigo VARCHAR(15),
    PlanDeEstudio_codigo VARCHAR(9),
    PRIMARY KEY (materia_codigo, correlativa_codigo,PlanDeEstudio_codigo),
    FOREIGN KEY (PlanDeEstudio_codigo) REFERENCES PlanDeEstudio(codigo) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (materia_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (correlativa_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE HistoriaAcademica (
    id SERIAL PRIMARY KEY,
    Persona_id_estudiante INT UNIQUE,
    PlanDeEstudio_codigo VARCHAR(9) NOT NULL,
    FOREIGN KEY (Persona_id_estudiante) REFERENCES Estudiante(Persona_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (PlanDeEstudio_codigo) REFERENCES PlanDeEstudio(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Renglon (
    id SERIAL PRIMARY KEY,
    fecha VARCHAR(12) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    nota DECIMAL(4,2),
    resultado VARCHAR(50) NOT NULL,
    historia_id INT NOT NULL,
    materia_codigo VARCHAR(15) NOT NULL,
    FOREIGN KEY (historia_id) REFERENCES HistoriaAcademica(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (materia_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Examen (
    id SERIAL PRIMARY KEY,
    fecha DATE NOT NULL,
    nota DECIMAL(4,2),
    Renglon_id INT UNIQUE,
    FOREIGN KEY (Renglon_id) REFERENCES Renglon(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE RegistroInscripcion (
    id SERIAL PRIMARY KEY,
    turno VARCHAR(50),
    anio INT NOT NULL,
    materia_codigo VARCHAR(15) NOT NULL,
    Persona_id_estudiante INT NOT NULL,
    FOREIGN KEY (Persona_id_estudiante) REFERENCES Estudiante(Persona_id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (materia_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Experiencia (
    id SERIAL PRIMARY KEY,
    examen_id INT UNIQUE,
    dificultad INT NOT NULL, -- Escala de 1 a 10
    dias_estudio INT NOT NULL,
    horas_diarias INT NOT NULL, -- promedio
    intentos_previos INT NOT NULL,
    modalidad VARCHAR (20), -- modalidad del examen
    recursos VARCHAR (200), -- con que recursos estudio de la materia
    motivacion VARCHAR(100), -- rindió por necesidad (última materia, correlatividad) o por preferencia.
    condiciones VARCHAR(100), -- Ambiente, claridad de consignas, calidad de corrección, tiempo suficiente.
    FOREIGN KEY (examen_id) REFERENCES Examen(id) ON DELETE CASCADE ON UPDATE CASCADE
);
