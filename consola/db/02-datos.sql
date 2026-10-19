USE tribu_consola;

INSERT INTO academia (id, razon_social, cuit) VALUES
    (1, 'Code Academy SRL',       '30-71234567-1'),
    (2, 'Instituto de Datos SAS', '30-71888444-2');

INSERT INTO curso (id, codigo, titulo, precio, estado, academia_id) VALUES
    (1, 'PRG-101', 'Java desde cero',             45000.00, 'PUBLICADO', 1),
    (2, 'PRG-205', 'Spring Boot aplicado',        72000.00, 'PUBLICADO', 1),
    (3, 'BDD-110', 'MySQL y modelado relacional', 58000.00, 'PUBLICADO', 2),
    (4, 'RED-120', 'Redes para desarrolladores',  39000.00, 'PUBLICADO', 2),
    (5, 'GES-130', 'Git y trabajo en equipo',     28000.00, 'BORRADOR',  1);

INSERT INTO leccion (curso_id, orden, titulo, tipo, duracion_segundos, cantidad_palabras, cantidad_preguntas) VALUES
    (1, 1, 'Que es la JVM',                  'VIDEO',        1080, NULL, NULL),
    (1, 2, 'Tipos de datos y variables',     'VIDEO',        1500, NULL, NULL),
    (1, 3, 'Estructuras de control',         'TEXTO',        NULL, 4000, NULL),
    (1, 4, 'Clases y objetos',               'VIDEO',        1800, NULL, NULL),
    (1, 5, 'Herencia y polimorfismo',        'VIDEO',        1680, NULL, NULL),
    (1, 6, 'Autoevaluacion del modulo',      'CUESTIONARIO', NULL, NULL, 10),
    (2, 1, 'Estructura de un proyecto Boot', 'VIDEO',        1320, NULL, NULL),
    (2, 2, 'Inyeccion de dependencias',      'VIDEO',        1560, NULL, NULL),
    (2, 3, 'Spring Data JPA',                'TEXTO',        NULL, 5200, NULL),
    (3, 1, 'Normalizacion hasta 3FN',        'TEXTO',        NULL, 7000, NULL),
    (3, 2, 'Claves foraneas e integridad',   'VIDEO',        1440, NULL, NULL),
    (3, 3, 'Ejercicios de modelado',         'CUESTIONARIO', NULL, NULL, 8),
    (4, 1, 'Modelo TCP/IP',                  'VIDEO',        1800, NULL, NULL),
    (4, 2, 'HTTP, HTTPS y TLS',              'VIDEO',        1620, NULL, NULL),
    (4, 3, 'Diagnostico de conectividad',    'CUESTIONARIO', NULL, NULL, 6),
    (5, 1, 'Ramas y fusiones',               'VIDEO',        1200, NULL, NULL);

INSERT INTO alumno (id, nombre, apellido, email) VALUES
    (1, 'Ana',   'Lopez', 'ana.lopez@gmail.com'),
    (2, 'Juan',  'Perez', 'juan.perez@gmail.com'),
    (3, 'Sofia', 'Rios',  'sofia.rios@gmail.com');

INSERT INTO inscripcion (id, alumno_id, curso_id, estado, fecha) VALUES
    (1, 1, 1, 'ACTIVA',     '2026-05-02 09:15:00'),
    (2, 2, 1, 'COMPLETADA', '2026-04-11 11:00:00'),
    (3, 3, 4, 'ACTIVA',     '2026-05-28 08:30:00');

INSERT INTO pago (inscripcion_id, medio, detalle, importe, estado, motivo_rechazo, fecha) VALUES
    (1, 'TARJETA_CREDITO', 'terminada en 3704 en 1 cuota', 45000.00, 'APROBADO', NULL, '2026-05-02 09:15:10'),
    (2, 'TARJETA_DEBITO',  'terminada en 3704',            45000.00, 'APROBADO', NULL, '2026-04-11 11:00:08'),
    (3, 'TRANSFERENCIA',   'desde CBU terminado en 2345',  37050.00, 'APROBADO', NULL, '2026-05-28 08:30:09');

INSERT INTO progreso_leccion (inscripcion_id, leccion_id, fecha) VALUES
    (1, 1, '2026-05-02 10:00:00'),
    (1, 2, '2026-05-03 19:30:00'),
    (1, 3, '2026-05-05 21:10:00'),
    (2, 1, '2026-04-12 08:00:00'),
    (2, 2, '2026-04-14 08:00:00'),
    (2, 3, '2026-04-18 08:00:00'),
    (2, 4, '2026-05-02 08:00:00'),
    (2, 5, '2026-05-20 08:00:00'),
    (2, 6, '2026-06-01 16:20:00'),
    (3, 13, '2026-05-29 22:15:00');
