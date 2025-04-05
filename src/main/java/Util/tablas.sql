CREATE TABLE Persona (
    id INT AUTO_INCREMENT PRIMARY KEY,
    dni INT NOT NULL,
    nombre_apellido VARCHAR(50) NOT NULL,
    mail VARCHAR(50) NOT NULL,
    telefono VARCHAR(15),
    usuario VARCHAR(100) NOT NULL UNIQUE,
    contrasenia VARCHAR(255) NOT NULL
);

CREATE TABLE Estudiante (
    nroRegistro INT PRIMARY KEY,
    Persona_id INT NOT NULL,
    FOREIGN KEY (Persona_id) REFERENCES Persona(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Administrador (
    Persona_id INT PRIMARY KEY,
    token INT NOT NULL,
    FOREIGN KEY (Persona_id) REFERENCES Persona(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE PlanDeEstudio (
    codigo VARCHAR(8) PRIMARY KEY,
    propuesta VARCHAR(30) NOT NULL
);

CREATE TABLE Materia (
    codigo VARCHAR(15),
    nombre VARCHAR(200) NOT NULL,
    PlanDeEstudio_codigo VARCHAR(8),
    PRIMARY KEY(codigo,PlanDeEstudio_codigo),
    FOREIGN KEY (PlanDeEstudio_codigo) REFERENCES PlanDeEstudio(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Correlativa (
    materia_codigo VARCHAR(15),
    correlativa_codigo VARCHAR(15),
    PlanDeEstudio_codigo VARCHAR(8),
    PRIMARY KEY (materia_codigo, correlativa_codigo,PlanDeEstudio_codigo),
    FOREIGN KEY (PlanDeEstudio_codigo) REFERENCES Materia(PlanDeEstudio_codigo) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (materia_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (correlativa_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE HistoriaAcademica (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nroRegistro_Estudiante INT UNIQUE,
    PlanDeEstudio_codigo VARCHAR(8) NOT NULL,
    FOREIGN KEY (nroRegistro_Estudiante) REFERENCES Estudiante(nroRegistro) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (PlanDeEstudio_codigo) REFERENCES PlanDeEstudio(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Renglon (
    historia_id INT PRIMARY KEY,
    materia_codigo VARCHAR(15) NOT NULL,
    fecha DATE NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    nota DECIMAL(4,2),
    resultado VARCHAR(50) NOT NULL,
    FOREIGN KEY (historia_id) REFERENCES HistoriaAcademica(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (materia_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Examen (
    id INT PRIMARY KEY AUTO_INCREMENT,
    historia_id_Renglon INT NOT NULL,
    fecha DATE NOT NULL,
    nota DECIMAL(4,2),
    materia_codigo VARCHAR(15) NOT NULL,
    FOREIGN KEY (historia_id_Renglon) REFERENCES Renglon(historia_id) ON DELETE CASCADE ON UPDATE CASCADE
    FOREIGN KEY (materia_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE RegistroInscripcion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    turno VARCHAR(50),
    anio INT NOT NULL,
    materia_codigo VARCHAR(15) NOT NULL,
    nroRegistro_Estudiante INT NOT NULL,
    FOREIGN KEY (nroRegistro_Estudiante) REFERENCES Estudiante(nroRegistro) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (materia_codigo) REFERENCES Materia(codigo) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE Experiencia (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nroRegistro_Estudiante INT NOT NULL,
    examen_id INT NOT NULL,
    dificultad INT, -- Escala de 1 a 10
    dias_estudio INT,
    horas_diarias INT,
    intentos_previos INT,
    modalidad VARCHAR (20), -- modalidad del examen
    recursos VARCHAR (200), -- con que recursos estudio de la materia
    motivacion VARCHAR(100) -- rindió por necesidad (última materia, correlatividad) o por preferencia.
    condiciones VARCHAR(100) -- Ambiente, claridad de consignas, calidad de corrección, tiempo suficiente.
    -- Aca se pueden poner muchas mas cosas
    FOREIGN KEY (examen_id) REFERENCES Examen(id) ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (nroRegistro_Estudiante) REFERENCES Estudiante(nroRegistro) ON DELETE CASCADE ON UPDATE CASCADE
);
