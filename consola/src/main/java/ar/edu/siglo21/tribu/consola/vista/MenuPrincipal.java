package ar.edu.siglo21.tribu.consola.vista;

import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;
import ar.edu.siglo21.tribu.consola.excepciones.PersistenciaException;
import ar.edu.siglo21.tribu.consola.excepciones.TribuException;
import ar.edu.siglo21.tribu.consola.modelo.Academia;
import ar.edu.siglo21.tribu.consola.modelo.Alumno;
import ar.edu.siglo21.tribu.consola.modelo.Curso;
import ar.edu.siglo21.tribu.consola.modelo.Inscripcion;
import ar.edu.siglo21.tribu.consola.modelo.Leccion;
import ar.edu.siglo21.tribu.consola.modelo.LeccionCuestionario;
import ar.edu.siglo21.tribu.consola.modelo.LeccionTexto;
import ar.edu.siglo21.tribu.consola.modelo.LeccionVideo;
import ar.edu.siglo21.tribu.consola.modelo.MedioPago;
import ar.edu.siglo21.tribu.consola.modelo.Pago;
import ar.edu.siglo21.tribu.consola.modelo.TarjetaCredito;
import ar.edu.siglo21.tribu.consola.modelo.TarjetaDebito;
import ar.edu.siglo21.tribu.consola.modelo.Transferencia;
import ar.edu.siglo21.tribu.consola.servicio.CriterioOrden;
import ar.edu.siglo21.tribu.consola.servicio.Plataforma;

import java.math.BigDecimal;

public class MenuPrincipal {

    private static final int SALIR = 0;

    private final Plataforma plataforma;
    private final Consola consola;

    public MenuPrincipal(Plataforma plataforma, Consola consola) {
        this.plataforma = plataforma;
        this.consola = consola;
    }

    public void iniciar() {
        int opcion;
        do {
            mostrarOpciones();
            opcion = consola.leerEntero("Elegí una opción: ", 0, 14);
            try {
                ejecutar(opcion);
            } catch (TribuException e) {
                consola.mostrarError(e.getMessage());
            } catch (PersistenciaException e) {
                String detalle = e.getCause() == null ? "" : ". Detalle: " + e.getCause().getMessage();
                consola.mostrarError(e.getMessage() + detalle);
            }
            if (opcion != SALIR) {
                consola.pausar();
            }
        } while (opcion != SALIR);
        consola.mostrar("Hasta luego.");
    }

    private void mostrarOpciones() {
        consola.mostrar("");
        consola.mostrar("========== TRIBU - Prototipo de consola ==========");
        consola.mostrar(" Cursos");
        consola.mostrar("   1. Listar cursos ordenados");
        consola.mostrar("   2. Buscar curso por código");
        consola.mostrar("   3. Buscar cursos por título");
        consola.mostrar("   4. Ver detalle de un curso");
        consola.mostrar(" Academias");
        consola.mostrar("   5. Registrar academia");
        consola.mostrar("   6. Crear curso");
        consola.mostrar("   7. Agregar lección a un curso");
        consola.mostrar("   8. Publicar curso");
        consola.mostrar(" Alumnos");
        consola.mostrar("   9. Registrar alumno");
        consola.mostrar("  10. Inscribirse a un curso");
        consola.mostrar("  11. Procesar pagos pendientes (" + plataforma.cantidadPagosPendientes() + " en la cola)");
        consola.mostrar("  12. Marcar lección como completada");
        consola.mostrar("  13. Deshacer la última lección completada");
        consola.mostrar("  14. Ver avance de un alumno");
        consola.mostrar("   0. Salir");
        consola.mostrar("==================================================");
    }

    private void ejecutar(int opcion) throws TribuException {
        switch (opcion) {
            case 1 -> listarCursos();
            case 2 -> buscarPorCodigo();
            case 3 -> buscarPorTitulo();
            case 4 -> verDetalleCurso();
            case 5 -> registrarAcademia();
            case 6 -> crearCurso();
            case 7 -> agregarLeccion();
            case 8 -> publicarCurso();
            case 9 -> registrarAlumno();
            case 10 -> inscribirse();
            case 11 -> procesarPagos();
            case 12 -> completarLeccion();
            case 13 -> deshacerLeccion();
            case 14 -> verAvance();
            case SALIR -> consola.mostrar("Cerrando el prototipo...");
            default -> consola.mostrarError("Opción inexistente");
        }
    }

    private void listarCursos() {
        consola.mostrarTitulo("Listar cursos");
        CriterioOrden[] criterios = CriterioOrden.values();
        for (int i = 0; i < criterios.length; i++) {
            consola.mostrar("  " + (i + 1) + ". " + criterios[i].getDescripcion());
        }
        int elegido = consola.leerEntero("Ordenar por: ", 1, criterios.length);
        mostrarCursos(plataforma.listarCursos(criterios[elegido - 1]));
    }

    private void buscarPorCodigo() throws TribuException {
        consola.mostrarTitulo("Buscar curso por código");
        Curso curso = plataforma.buscarCursoPorCodigo(consola.leerTexto("Código (por ejemplo PRG-101): "));
        mostrarDetalle(curso);
    }

    private void buscarPorTitulo() {
        consola.mostrarTitulo("Buscar cursos por título");
        String texto = consola.leerTexto("Texto a buscar: ");
        ListaEnlazada<Curso> encontrados = plataforma.buscarCursosPorTitulo(texto);
        if (encontrados.estaVacia()) {
            consola.mostrar("No hay cursos cuyo título contenga \"" + texto + "\".");
        } else {
            mostrarCursos(encontrados);
        }
    }

    private void verDetalleCurso() throws TribuException {
        consola.mostrarTitulo("Detalle de un curso");
        mostrarDetalle(pedirCurso());
    }

    private void registrarAcademia() throws TribuException {
        consola.mostrarTitulo("Registrar academia");
        String razonSocial = consola.leerTexto("Razón social: ");
        String cuit = consola.leerTexto("CUIT (00-00000000-0): ");
        Academia academia = plataforma.registrarAcademia(razonSocial, cuit);
        consola.mostrarExito("Academia registrada: " + academia);
    }

    private void crearCurso() throws TribuException {
        consola.mostrarTitulo("Crear curso");
        Academia academia = pedirAcademia();
        String codigo = consola.leerTexto("Código (tres letras, guion y tres números): ");
        String titulo = consola.leerTexto("Título: ");
        BigDecimal precio = consola.leerImporte("Precio en pesos: ");
        Curso curso = plataforma.crearCurso(codigo, titulo, precio, academia);
        consola.mostrarExito("Curso " + curso.getCodigo() + " creado en borrador.");
        while (consola.confirmar("¿Querés agregarle una lección?")) {
            try {
                plataforma.agregarLeccion(curso, pedirLeccion(curso.getLecciones().tamanio() + 1));
                consola.mostrarExito("Lección agregada. Duración total del curso: " + curso.getDuracionTotalMinutos() + " min");
            } catch (TribuException e) {
                consola.mostrarError(e.getMessage());
            }
        }
    }

    private void agregarLeccion() throws TribuException {
        consola.mostrarTitulo("Agregar lección");
        Curso curso = pedirCurso();
        plataforma.agregarLeccion(curso, pedirLeccion(curso.getLecciones().tamanio() + 1));
        consola.mostrarExito("Lección agregada. Duración total del curso: " + curso.getDuracionTotalMinutos() + " min");
    }

    private void publicarCurso() throws TribuException {
        consola.mostrarTitulo("Publicar curso");
        Curso curso = pedirCurso();
        plataforma.publicarCurso(curso);
        consola.mostrarExito("El curso " + curso.getTitulo() + " ya aparece en el catálogo.");
    }

    private void registrarAlumno() throws TribuException {
        consola.mostrarTitulo("Registrar alumno");
        String nombre = consola.leerTexto("Nombre: ");
        String apellido = consola.leerTexto("Apellido: ");
        String email = consola.leerTexto("Email: ");
        Alumno alumno = plataforma.registrarAlumno(nombre, apellido, email);
        consola.mostrarExito("Alumno registrado: " + alumno);
    }

    private void inscribirse() throws TribuException {
        consola.mostrarTitulo("Inscribirse a un curso");
        Alumno alumno = pedirAlumno();
        Curso curso = pedirCurso();
        MedioPago medio = pedirMedioPago(alumno);
        consola.mostrar("Importe a cobrar: $" + String.format("%,.2f", medio.calcularImporteFinal(curso.getPrecio()))
                + " (precio de lista $" + String.format("%,.2f", curso.getPrecio()) + ")");
        plataforma.inscribir(alumno, curso, medio);
        consola.mostrarExito("Inscripción registrada. El pago quedó en la cola, en la posición "
                + plataforma.cantidadPagosPendientes() + ".");
    }

    private void procesarPagos() throws TribuException {
        consola.mostrarTitulo("Procesar pagos pendientes");
        if (plataforma.cantidadPagosPendientes() == 0) {
            consola.mostrar("No hay pagos en la cola.");
            return;
        }
        while (plataforma.cantidadPagosPendientes() > 0) {
            Pago pago = plataforma.procesarSiguientePago();
            Inscripcion inscripcion = pago.getInscripcion();
            String resumen = inscripcion.getAlumno().getNombreCompleto() + " - " + inscripcion.getCurso().getTitulo()
                    + " - " + pago.getMedio() + " " + pago.getDetalle()
                    + " - $" + String.format("%,.2f", pago.getImporte());
            if (pago.fueAprobado()) {
                consola.mostrarExito("APROBADO: " + resumen);
            } else {
                consola.mostrarError("RECHAZADO (" + pago.getMotivoRechazo() + "): " + resumen);
            }
        }
    }

    private void completarLeccion() throws TribuException {
        consola.mostrarTitulo("Marcar lección como completada");
        Alumno alumno = pedirAlumno();
        Curso curso = pedirCurso();
        for (Leccion leccion : curso.getLecciones()) {
            consola.mostrar("  " + leccion);
        }
        int orden = consola.leerEntero("Número de lección: ", 1, Integer.MAX_VALUE);
        Inscripcion inscripcion = plataforma.completarLeccion(alumno, curso, orden);
        consola.mostrarExito("Avance: " + inscripcion.getCantidadCompletadas() + " de "
                + curso.getLecciones().tamanio() + " lecciones (" + inscripcion.porcentajeAvance() + "%). Estado: "
                + inscripcion.getEstado());
    }

    private void deshacerLeccion() throws TribuException {
        consola.mostrarTitulo("Deshacer la última lección completada");
        Alumno alumno = pedirAlumno();
        Curso curso = pedirCurso();
        Leccion leccion = plataforma.deshacerUltimaLeccion(alumno, curso);
        consola.mostrarExito("Se desmarcó la lección \"" + leccion.getTitulo() + "\".");
    }

    private void verAvance() throws TribuException {
        consola.mostrarTitulo("Avance del alumno");
        Alumno alumno = pedirAlumno();
        ListaEnlazada<Inscripcion> inscripciones = plataforma.inscripcionesDe(alumno);
        if (inscripciones.estaVacia()) {
            consola.mostrar(alumno.getNombreCompleto() + " no tiene inscripciones.");
            return;
        }
        for (Inscripcion inscripcion : inscripciones) {
            Leccion ultima = inscripcion.getUltimaCompletada();
            consola.mostrar(String.format("  %-30s %-15s %s %3d%%  última: %s",
                    inscripcion.getCurso().getTitulo(), inscripcion.getEstado(),
                    barra(inscripcion.porcentajeAvance()), inscripcion.porcentajeAvance(),
                    ultima == null ? "-" : ultima.getTitulo()));
        }
    }

    private String barra(int porcentaje) {
        StringBuilder barra = new StringBuilder("[");
        for (int i = 0; i < 10; i++) {
            barra.append(i < porcentaje / 10 ? '#' : '.');
        }
        return barra.append(']').toString();
    }

    private Curso pedirCurso() throws TribuException {
        return plataforma.buscarCursoPorCodigo(consola.leerTexto("Código del curso: "));
    }

    private Alumno pedirAlumno() throws TribuException {
        return plataforma.buscarAlumnoPorEmail(consola.leerTexto("Email del alumno: "));
    }

    private Academia pedirAcademia() {
        ListaEnlazada<Academia> academias = plataforma.getAcademias();
        int numero = 1;
        for (Academia academia : academias) {
            consola.mostrar("  " + numero++ + ". " + academia);
        }
        int elegida = consola.leerEntero("Academia: ", 1, academias.tamanio());
        return academias.obtener(elegida - 1);
    }

    private Leccion pedirLeccion(int orden) throws TribuException {
        String titulo = consola.leerTexto("Título de la lección: ");
        consola.mostrar("  1. Video   2. Texto   3. Cuestionario");
        int tipo = consola.leerEntero("Tipo: ", 1, 3);
        Leccion leccion;
        if (tipo == 1) {
            leccion = new LeccionVideo(0, titulo, orden, consola.leerEntero("Duración en segundos: ", 1, 36000));
        } else if (tipo == 2) {
            leccion = new LeccionTexto(0, titulo, orden, consola.leerEntero("Cantidad de palabras: ", 1, 100000));
        } else {
            leccion = new LeccionCuestionario(0, titulo, orden, consola.leerEntero("Cantidad de preguntas: ", 1, 100));
        }
        return leccion;
    }

    private MedioPago pedirMedioPago(Alumno alumno) throws TribuException {
        consola.mostrar("  1. Tarjeta de crédito (1 cuota sin recargo, 3 cuotas +10%, 6 cuotas +20%)");
        consola.mostrar("  2. Tarjeta de débito");
        consola.mostrar("  3. Transferencia bancaria (5% de descuento)");
        int tipo = consola.leerEntero("Medio de pago: ", 1, 3);
        String titular = alumno.getNombreCompleto();
        return switch (tipo) {
            case 1 -> new TarjetaCredito(titular, consola.leerTexto("Número de tarjeta: "),
                    consola.leerEntero("Cuotas (1, 3 o 6): ", 1, 6));
            case 2 -> new TarjetaDebito(titular, consola.leerTexto("Número de tarjeta: "));
            default -> new Transferencia(titular, consola.leerTexto("CBU (22 dígitos): "));
        };
    }

    private void mostrarCursos(ListaEnlazada<Curso> cursos) {
        consola.mostrar(String.format("%-8s %-30s %12s %8s  %-9s %s", "CÓDIGO", "TÍTULO", "PRECIO", "DURAC.", "ESTADO", "ACADEMIA"));
        for (Curso curso : cursos) {
            consola.mostrar(curso.toString());
        }
        consola.mostrar(cursos.tamanio() + " curso(s).");
    }

    private void mostrarDetalle(Curso curso) {
        consola.mostrar(curso.getCodigo() + " - " + curso.getTitulo());
        consola.mostrar("Academia: " + curso.getAcademia());
        consola.mostrar("Precio: $" + String.format("%,.2f", curso.getPrecio()) + "   Estado: " + curso.getEstado());
        consola.mostrar("Lecciones (" + curso.getLecciones().tamanio() + ", " + curso.getDuracionTotalMinutos() + " min en total):");
        for (Leccion leccion : curso.getLecciones()) {
            consola.mostrar("  " + leccion);
        }
    }
}
