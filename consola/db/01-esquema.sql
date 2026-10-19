CREATE DATABASE IF NOT EXISTS tribu_consola CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE tribu_consola;

CREATE TABLE academia (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    razon_social VARCHAR(150) NOT NULL,
    cuit         CHAR(13)     NOT NULL UNIQUE
);

CREATE TABLE curso (
    id          INT AUTO_INCREMENT PRIMARY KEY,
    codigo      CHAR(7)       NOT NULL UNIQUE,
    titulo      VARCHAR(150)  NOT NULL,
    precio      DECIMAL(10,2) NOT NULL CHECK (precio > 0),
    estado      ENUM('BORRADOR','PUBLICADO') NOT NULL DEFAULT 'BORRADOR',
    academia_id INT           NOT NULL,
    CONSTRAINT fk_curso_academia FOREIGN KEY (academia_id) REFERENCES academia (id)
);

CREATE TABLE leccion (
    id                 INT AUTO_INCREMENT PRIMARY KEY,
    curso_id           INT          NOT NULL,
    orden              INT          NOT NULL,
    titulo             VARCHAR(150) NOT NULL,
    tipo               ENUM('VIDEO','TEXTO','CUESTIONARIO') NOT NULL,
    duracion_segundos  INT NULL,
    cantidad_palabras  INT NULL,
    cantidad_preguntas INT NULL,
    CONSTRAINT fk_leccion_curso FOREIGN KEY (curso_id) REFERENCES curso (id) ON DELETE CASCADE,
    CONSTRAINT uq_leccion_orden UNIQUE (curso_id, orden)
);

CREATE TABLE alumno (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    nombre   VARCHAR(80)  NOT NULL,
    apellido VARCHAR(80)  NOT NULL,
    email    VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE inscripcion (
    id        INT AUTO_INCREMENT PRIMARY KEY,
    alumno_id INT      NOT NULL,
    curso_id  INT      NOT NULL,
    estado    ENUM('PENDIENTE_PAGO','ACTIVA','COMPLETADA') NOT NULL DEFAULT 'PENDIENTE_PAGO',
    fecha     DATETIME NOT NULL,
    CONSTRAINT fk_inscripcion_alumno FOREIGN KEY (alumno_id) REFERENCES alumno (id),
    CONSTRAINT fk_inscripcion_curso FOREIGN KEY (curso_id) REFERENCES curso (id),
    CONSTRAINT uq_inscripcion UNIQUE (alumno_id, curso_id)
);

CREATE TABLE progreso_leccion (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    inscripcion_id INT      NOT NULL,
    leccion_id     INT      NOT NULL,
    fecha          DATETIME NOT NULL,
    CONSTRAINT fk_progreso_inscripcion FOREIGN KEY (inscripcion_id) REFERENCES inscripcion (id),
    CONSTRAINT fk_progreso_leccion FOREIGN KEY (leccion_id) REFERENCES leccion (id),
    CONSTRAINT uq_progreso UNIQUE (inscripcion_id, leccion_id)
);

CREATE TABLE pago (
    id             INT AUTO_INCREMENT PRIMARY KEY,
    inscripcion_id INT           NOT NULL,
    medio          ENUM('TARJETA_CREDITO','TARJETA_DEBITO','TRANSFERENCIA') NOT NULL,
    detalle        VARCHAR(80)   NOT NULL,
    importe        DECIMAL(10,2) NOT NULL,
    estado         ENUM('APROBADO','RECHAZADO') NOT NULL,
    motivo_rechazo VARCHAR(120)  NULL,
    fecha          DATETIME      NOT NULL,
    CONSTRAINT fk_pago_inscripcion FOREIGN KEY (inscripcion_id) REFERENCES inscripcion (id)
);
