package ar.edu.siglo21.tribu.consola.modelo;

import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;
import ar.edu.siglo21.tribu.consola.excepciones.ReglaNegocioException;
import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;

import java.math.BigDecimal;
import java.util.regex.Pattern;

public class Curso implements Comparable<Curso> {

    private static final Pattern CODIGO = Pattern.compile("[A-Z]{3}-\\d{3}");

    private int id;
    private String codigo;
    private String titulo;
    private BigDecimal precio;
    private EstadoCurso estado;
    private final Academia academia;
    private final ListaEnlazada<Leccion> lecciones;

    public Curso(int id, String codigo, String titulo, BigDecimal precio, EstadoCurso estado, Academia academia)
            throws ValidacionException {
        if (academia == null) {
            throw new ValidacionException("El curso debe pertenecer a una academia");
        }
        this.id = id;
        setCodigo(codigo);
        setTitulo(titulo);
        setPrecio(precio);
        this.estado = estado;
        this.academia = academia;
        this.lecciones = new ListaEnlazada<>();
    }

    public Curso(String codigo, String titulo, BigDecimal precio, Academia academia) throws ValidacionException {
        this(0, codigo, titulo, precio, EstadoCurso.BORRADOR, academia);
    }

    public void agregarLeccion(Leccion leccion) throws ReglaNegocioException {
        for (Leccion existente : lecciones) {
            if (existente.getOrden() == leccion.getOrden()) {
                throw new ReglaNegocioException("Ya existe una lección con el orden " + leccion.getOrden());
            }
        }
        lecciones.agregar(leccion);
    }

    public void publicar() throws ReglaNegocioException {
        if (estado == EstadoCurso.PUBLICADO) {
            throw new ReglaNegocioException("El curso " + codigo + " ya está publicado");
        }
        if (lecciones.estaVacia()) {
            throw new ReglaNegocioException("No se puede publicar un curso sin lecciones");
        }
        estado = EstadoCurso.PUBLICADO;
    }

    public int getDuracionTotalMinutos() {
        int total = 0;
        for (Leccion leccion : lecciones) {
            total += leccion.duracionEstimadaMinutos();
        }
        return total;
    }

    public Leccion buscarLeccionPorOrden(int orden) {
        for (Leccion leccion : lecciones) {
            if (leccion.getOrden() == orden) {
                return leccion;
            }
        }
        return null;
    }

    public boolean estaPublicado() {
        return estado == EstadoCurso.PUBLICADO;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) throws ValidacionException {
        String limpio = Validaciones.texto(codigo, "código", 7).toUpperCase();
        if (!CODIGO.matcher(limpio).matches()) {
            throw new ValidacionException("El código debe tener tres letras, un guion y tres números (por ejemplo PRG-101)");
        }
        this.codigo = limpio;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) throws ValidacionException {
        this.titulo = Validaciones.texto(titulo, "título", 150);
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) throws ValidacionException {
        this.precio = Validaciones.importePositivo(precio, "precio");
    }

    public EstadoCurso getEstado() {
        return estado;
    }

    public Academia getAcademia() {
        return academia;
    }

    public ListaEnlazada<Leccion> getLecciones() {
        return lecciones;
    }

    @Override
    public int compareTo(Curso otro) {
        return codigo.compareTo(otro.codigo);
    }

    @Override
    public String toString() {
        return String.format("%-8s %-30s $%,11.2f %4d min  %-9s %s",
                codigo, titulo, precio, getDuracionTotalMinutos(), estado, academia.getRazonSocial());
    }
}
