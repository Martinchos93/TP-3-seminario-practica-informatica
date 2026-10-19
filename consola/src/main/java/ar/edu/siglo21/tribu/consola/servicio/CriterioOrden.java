package ar.edu.siglo21.tribu.consola.servicio;

import ar.edu.siglo21.tribu.consola.modelo.Curso;

import java.util.Comparator;

public enum CriterioOrden {
    CODIGO("Código", Comparator.naturalOrder()),
    TITULO("Título", Comparator.comparing(Curso::getTitulo, String.CASE_INSENSITIVE_ORDER)),
    PRECIO_MENOR("Precio, de menor a mayor", Comparator.comparing(Curso::getPrecio)),
    PRECIO_MAYOR("Precio, de mayor a menor", Comparator.comparing(Curso::getPrecio).reversed()),
    DURACION("Duración total", Comparator.comparingInt(Curso::getDuracionTotalMinutos));

    private final String descripcion;
    private final Comparator<Curso> comparador;

    CriterioOrden(String descripcion, Comparator<Curso> comparador) {
        this.descripcion = descripcion;
        this.comparador = comparador;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public Comparator<Curso> getComparador() {
        return comparador;
    }
}
