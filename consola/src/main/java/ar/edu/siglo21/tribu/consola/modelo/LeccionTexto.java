package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

public class LeccionTexto extends Leccion {

    private static final int PALABRAS_POR_MINUTO = 200;

    private final int cantidadPalabras;

    public LeccionTexto(int id, String titulo, int orden, int cantidadPalabras) throws ValidacionException {
        super(id, titulo, orden);
        this.cantidadPalabras = Validaciones.enteroPositivo(cantidadPalabras, "cantidad de palabras");
    }

    public int getCantidadPalabras() {
        return cantidadPalabras;
    }

    @Override
    public int duracionEstimadaMinutos() {
        return (int) Math.ceil((double) cantidadPalabras / PALABRAS_POR_MINUTO);
    }

    @Override
    public String getTipo() {
        return "TEXTO";
    }
}
