package ar.edu.siglo21.tribu.consola.servicio;

import ar.edu.siglo21.tribu.consola.algoritmos.Busqueda;
import ar.edu.siglo21.tribu.consola.algoritmos.Ordenamiento;
import ar.edu.siglo21.tribu.consola.estructuras.Cola;
import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;
import ar.edu.siglo21.tribu.consola.excepciones.EntidadNoEncontradaException;
import ar.edu.siglo21.tribu.consola.excepciones.PagoRechazadoException;
import ar.edu.siglo21.tribu.consola.excepciones.ReglaNegocioException;
import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;
import ar.edu.siglo21.tribu.consola.modelo.Academia;
import ar.edu.siglo21.tribu.consola.modelo.Alumno;
import ar.edu.siglo21.tribu.consola.modelo.Curso;
import ar.edu.siglo21.tribu.consola.modelo.EstadoInscripcion;
import ar.edu.siglo21.tribu.consola.modelo.EstadoPago;
import ar.edu.siglo21.tribu.consola.modelo.Inscripcion;
import ar.edu.siglo21.tribu.consola.modelo.Leccion;
import ar.edu.siglo21.tribu.consola.modelo.MedioPago;
import ar.edu.siglo21.tribu.consola.modelo.Pago;
import ar.edu.siglo21.tribu.consola.persistencia.AcademiaDao;
import ar.edu.siglo21.tribu.consola.persistencia.AlumnoDao;
import ar.edu.siglo21.tribu.consola.persistencia.CursoDao;
import ar.edu.siglo21.tribu.consola.persistencia.InscripcionDao;

import java.math.BigDecimal;

public class Plataforma {

    private final AcademiaDao academiaDao;
    private final AlumnoDao alumnoDao;
    private final CursoDao cursoDao;
    private final InscripcionDao inscripcionDao;

    private final ListaEnlazada<Academia> academias;
    private final ListaEnlazada<Alumno> alumnos;
    private final ListaEnlazada<Curso> cursos;
    private final ListaEnlazada<Inscripcion> inscripciones;
    private final Cola<SolicitudPago> pagosPendientes;

    public Plataforma() {
        academiaDao = new AcademiaDao();
        alumnoDao = new AlumnoDao();
        academias = academiaDao.listarTodos();
        alumnos = alumnoDao.listarTodos();
        cursoDao = new CursoDao(academias);
        cursos = cursoDao.listarTodos();
        inscripcionDao = new InscripcionDao(alumnos, cursos);
        inscripciones = inscripcionDao.listarTodos();
        pagosPendientes = new Cola<>();
    }

    public ListaEnlazada<Curso> listarCursos(CriterioOrden criterio) {
        return Ordenamiento.quickSort(cursos, criterio.getComparador());
    }

    public Curso buscarCursoPorCodigo(String codigo) throws EntidadNoEncontradaException {
        ListaEnlazada<Curso> ordenados = Ordenamiento.quickSort(cursos, CriterioOrden.CODIGO.getComparador());
        Curso curso = Busqueda.binaria(ordenados, codigo.trim().toUpperCase(), Curso::getCodigo);
        if (curso == null) {
            throw new EntidadNoEncontradaException("No existe un curso con el código " + codigo);
        }
        return curso;
    }

    public ListaEnlazada<Curso> buscarCursosPorTitulo(String texto) {
        String buscado = texto.trim().toLowerCase();
        return Busqueda.secuencial(cursos, curso -> curso.getTitulo().toLowerCase().contains(buscado));
    }

    public ListaEnlazada<Academia> getAcademias() {
        return academias;
    }

    public ListaEnlazada<Alumno> getAlumnos() {
        return alumnos;
    }

    public Academia registrarAcademia(String razonSocial, String cuit) throws ValidacionException, ReglaNegocioException {
        Academia academia = new Academia(razonSocial, cuit);
        for (Academia existente : academias) {
            if (existente.getCuit().equals(academia.getCuit())) {
                throw new ReglaNegocioException("La CUIT " + cuit + " ya pertenece a " + existente.getRazonSocial());
            }
        }
        academiaDao.guardar(academia);
        academias.agregar(academia);
        return academia;
    }

    public Curso crearCurso(String codigo, String titulo, BigDecimal precio, Academia academia)
            throws ValidacionException, ReglaNegocioException {
        Curso curso = new Curso(codigo, titulo, precio, academia);
        try {
            buscarCursoPorCodigo(curso.getCodigo());
            throw new ReglaNegocioException("Ya existe un curso con el código " + curso.getCodigo());
        } catch (EntidadNoEncontradaException e) {
            cursoDao.guardar(curso);
            cursos.agregar(curso);
            return curso;
        }
    }

    public void agregarLeccion(Curso curso, Leccion leccion) throws ReglaNegocioException {
        if (curso.estaPublicado()) {
            throw new ReglaNegocioException("No se pueden agregar lecciones a un curso publicado");
        }
        curso.agregarLeccion(leccion);
        cursoDao.agregarLeccion(curso, leccion);
    }

    public void publicarCurso(Curso curso) throws ReglaNegocioException {
        curso.publicar();
        cursoDao.actualizarEstado(curso);
    }

    public Alumno registrarAlumno(String nombre, String apellido, String email)
            throws ValidacionException, ReglaNegocioException {
        Alumno alumno = new Alumno(nombre, apellido, email);
        for (Alumno existente : alumnos) {
            if (existente.getEmail().equals(alumno.getEmail())) {
                throw new ReglaNegocioException("Ya hay un alumno registrado con el email " + alumno.getEmail());
            }
        }
        alumnoDao.guardar(alumno);
        alumnos.agregar(alumno);
        return alumno;
    }

    public Alumno buscarAlumnoPorEmail(String email) throws EntidadNoEncontradaException {
        ListaEnlazada<Alumno> encontrados = Busqueda.secuencial(alumnos,
                alumno -> alumno.getEmail().equalsIgnoreCase(email.trim()));
        if (encontrados.estaVacia()) {
            throw new EntidadNoEncontradaException("No hay un alumno registrado con el email " + email);
        }
        return encontrados.obtener(0);
    }

    public Inscripcion inscribir(Alumno alumno, Curso curso, MedioPago medioPago) throws ReglaNegocioException {
        if (!curso.estaPublicado()) {
            throw new ReglaNegocioException("El curso " + curso.getCodigo() + " todavía no está publicado");
        }
        Inscripcion inscripcion = buscarInscripcion(alumno, curso);
        if (inscripcion == null) {
            inscripcion = new Inscripcion(alumno, curso);
            inscripcionDao.guardar(inscripcion);
            inscripciones.agregar(inscripcion);
        } else if (inscripcion.getEstado() != EstadoInscripcion.PENDIENTE_PAGO) {
            throw new ReglaNegocioException(alumno.getNombreCompleto() + " ya está inscripto y pagó " + curso.getTitulo());
        }
        pagosPendientes.encolar(new SolicitudPago(inscripcion, medioPago));
        return inscripcion;
    }

    public int cantidadPagosPendientes() {
        return pagosPendientes.tamanio();
    }

    public Pago procesarSiguientePago() throws ReglaNegocioException {
        if (pagosPendientes.estaVacia()) {
            throw new ReglaNegocioException("No hay pagos pendientes en la cola");
        }
        SolicitudPago solicitud = pagosPendientes.desencolar();
        Inscripcion inscripcion = solicitud.getInscripcion();
        MedioPago medio = solicitud.getMedioPago();
        BigDecimal importe = medio.calcularImporteFinal(inscripcion.getCurso().getPrecio());
        Pago pago;
        try {
            medio.autorizar();
            inscripcion.activar();
            pago = new Pago(inscripcion, medio, importe, EstadoPago.APROBADO, null);
        } catch (PagoRechazadoException e) {
            pago = new Pago(inscripcion, medio, importe, EstadoPago.RECHAZADO, e.getMotivo());
        }
        inscripcionDao.registrarPago(pago);
        return pago;
    }

    public Inscripcion completarLeccion(Alumno alumno, Curso curso, int orden)
            throws EntidadNoEncontradaException, ReglaNegocioException {
        Inscripcion inscripcion = obtenerInscripcion(alumno, curso);
        Leccion leccion = curso.buscarLeccionPorOrden(orden);
        if (leccion == null) {
            throw new EntidadNoEncontradaException("El curso no tiene una lección con el número " + orden);
        }
        inscripcion.completarLeccion(leccion);
        inscripcionDao.registrarAvance(inscripcion, leccion);
        return inscripcion;
    }

    public Leccion deshacerUltimaLeccion(Alumno alumno, Curso curso)
            throws EntidadNoEncontradaException, ReglaNegocioException {
        Inscripcion inscripcion = obtenerInscripcion(alumno, curso);
        Leccion leccion = inscripcion.deshacerUltimaLeccion();
        inscripcionDao.eliminarAvance(inscripcion, leccion);
        return leccion;
    }

    public ListaEnlazada<Inscripcion> inscripcionesDe(Alumno alumno) {
        return Busqueda.secuencial(inscripciones, inscripcion -> inscripcion.getAlumno() == alumno);
    }

    private Inscripcion obtenerInscripcion(Alumno alumno, Curso curso) throws EntidadNoEncontradaException {
        Inscripcion inscripcion = buscarInscripcion(alumno, curso);
        if (inscripcion == null) {
            throw new EntidadNoEncontradaException(alumno.getNombreCompleto() + " no está inscripto en " + curso.getTitulo());
        }
        return inscripcion;
    }

    private Inscripcion buscarInscripcion(Alumno alumno, Curso curso) {
        for (Inscripcion inscripcion : inscripciones) {
            if (inscripcion.getAlumno() == alumno && inscripcion.getCurso() == curso) {
                return inscripcion;
            }
        }
        return null;
    }
}
