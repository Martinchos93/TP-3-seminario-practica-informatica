package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

public class LeccionCuestionario extends Leccion {

    private static final int MINUTOS_POR_PREGUNTA = 2;

    private final int cantidadPreguntas;

    public LeccionCuestionario(int id, String titulo, int orden, int cantidadPreguntas) throws ValidacionException {
        super(id, titulo, orden);
        this.cantidadPreguntas = Validaciones.enteroPositivo(cantidadPreguntas, "cantidad de preguntas");
    }

    public int getCantidadPreguntas() {
        return cantidadPreguntas;
    }

    @Override
    public int duracionEstimadaMinutos() {
        return cantidadPreguntas * MINUTOS_POR_PREGUNTA;
    }

    @Override
    public String getTipo() {
        return "CUESTIONARIO";
    }
}
