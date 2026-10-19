package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

public abstract class Leccion {

    private int id;
    private String titulo;
    private int orden;

    protected Leccion(int id, String titulo, int orden) throws ValidacionException {
        this.id = id;
        this.titulo = Validaciones.texto(titulo, "título de la lección", 150);
        this.orden = Validaciones.enteroPositivo(orden, "orden");
    }

    public abstract int duracionEstimadaMinutos();

    public abstract String getTipo();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public int getOrden() {
        return orden;
    }

    @Override
    public String toString() {
        return String.format("%d. %-34s %-13s %3d min", orden, titulo, getTipo(), duracionEstimadaMinutos());
    }
}
