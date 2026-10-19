package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.estructuras.Pila;
import ar.edu.siglo21.tribu.consola.excepciones.ReglaNegocioException;

import java.time.LocalDateTime;

public class Inscripcion {

    private int id;
    private final Alumno alumno;
    private final Curso curso;
    private EstadoInscripcion estado;
    private final LocalDateTime fecha;
    private final Pila<Leccion> leccionesCompletadas;

    public Inscripcion(int id, Alumno alumno, Curso curso, EstadoInscripcion estado, LocalDateTime fecha) {
        this.id = id;
        this.alumno = alumno;
        this.curso = curso;
        this.estado = estado;
        this.fecha = fecha;
        this.leccionesCompletadas = new Pila<>();
    }

    public Inscripcion(Alumno alumno, Curso curso) {
        this(0, alumno, curso, EstadoInscripcion.PENDIENTE_PAGO, LocalDateTime.now());
    }

    public void activar() throws ReglaNegocioException {
        if (estado != EstadoInscripcion.PENDIENTE_PAGO) {
            throw new ReglaNegocioException("La inscripción ya fue pagada");
        }
        estado = EstadoInscripcion.ACTIVA;
    }

    public void completarLeccion(Leccion leccion) throws ReglaNegocioException {
        if (estado == EstadoInscripcion.PENDIENTE_PAGO) {
            throw new ReglaNegocioException("El curso todavía no está pagado");
        }
        if (estado == EstadoInscripcion.COMPLETADA) {
            throw new ReglaNegocioException("El curso ya fue completado");
        }
        if (yaCompleto(leccion)) {
            throw new ReglaNegocioException("La lección \"" + leccion.getTitulo() + "\" ya estaba completada");
        }
        leccionesCompletadas.apilar(leccion);
        if (leccionesCompletadas.tamanio() == curso.getLecciones().tamanio()) {
            estado = EstadoInscripcion.COMPLETADA;
        }
    }

    public Leccion deshacerUltimaLeccion() throws ReglaNegocioException {
        if (leccionesCompletadas.estaVacia()) {
            throw new ReglaNegocioException("No hay lecciones completadas para deshacer");
        }
        Leccion leccion = leccionesCompletadas.desapilar();
        if (estado == EstadoInscripcion.COMPLETADA) {
            estado = EstadoInscripcion.ACTIVA;
        }
        return leccion;
    }

    public void restaurarLeccionCompletada(Leccion leccion) {
        leccionesCompletadas.apilar(leccion);
    }

    public boolean yaCompleto(Leccion buscada) {
        Pila<Leccion> auxiliar = new Pila<>();
        boolean encontrada = false;
        while (!leccionesCompletadas.estaVacia()) {
            Leccion leccion = leccionesCompletadas.desapilar();
            if (leccion.getOrden() == buscada.getOrden()) {
                encontrada = true;
            }
            auxiliar.apilar(leccion);
        }
        while (!auxiliar.estaVacia()) {
            leccionesCompletadas.apilar(auxiliar.desapilar());
        }
        return encontrada;
    }

    public int porcentajeAvance() {
        int total = curso.getLecciones().tamanio();
        if (total == 0) {
            return 0;
        }
        return leccionesCompletadas.tamanio() * 100 / total;
    }

    public Leccion getUltimaCompletada() {
        return leccionesCompletadas.estaVacia() ? null : leccionesCompletadas.verTope();
    }

    public int getCantidadCompletadas() {
        return leccionesCompletadas.tamanio();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Alumno getAlumno() {
        return alumno;
    }

    public Curso getCurso() {
        return curso;
    }

    public EstadoInscripcion getEstado() {
        return estado;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
}
