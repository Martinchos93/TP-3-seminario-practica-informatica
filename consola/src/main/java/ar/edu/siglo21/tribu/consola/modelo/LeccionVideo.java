package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

public class LeccionVideo extends Leccion {

    private final int duracionSegundos;

    public LeccionVideo(int id, String titulo, int orden, int duracionSegundos) throws ValidacionException {
        super(id, titulo, orden);
        this.duracionSegundos = Validaciones.enteroPositivo(duracionSegundos, "duración del video");
    }

    public int getDuracionSegundos() {
        return duracionSegundos;
    }

    @Override
    public int duracionEstimadaMinutos() {
        return (int) Math.ceil(duracionSegundos / 60.0);
    }

    @Override
    public String getTipo() {
        return "VIDEO";
    }
}
