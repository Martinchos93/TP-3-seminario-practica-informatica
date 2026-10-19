package ar.edu.siglo21.tribu.consola;

import ar.edu.siglo21.tribu.consola.excepciones.PagoRechazadoException;
import ar.edu.siglo21.tribu.consola.excepciones.ReglaNegocioException;
import ar.edu.siglo21.tribu.consola.excepciones.ValidacionException;
import ar.edu.siglo21.tribu.consola.modelo.Academia;
import ar.edu.siglo21.tribu.consola.modelo.Alumno;
import ar.edu.siglo21.tribu.consola.modelo.Curso;
import ar.edu.siglo21.tribu.consola.modelo.EstadoInscripcion;
import ar.edu.siglo21.tribu.consola.modelo.Inscripcion;
import ar.edu.siglo21.tribu.consola.modelo.Leccion;
import ar.edu.siglo21.tribu.consola.modelo.LeccionCuestionario;
import ar.edu.siglo21.tribu.consola.modelo.LeccionTexto;
import ar.edu.siglo21.tribu.consola.modelo.LeccionVideo;
import ar.edu.siglo21.tribu.consola.modelo.MedioPago;
import ar.edu.siglo21.tribu.consola.modelo.TarjetaCredito;
import ar.edu.siglo21.tribu.consola.modelo.TarjetaDebito;
import ar.edu.siglo21.tribu.consola.modelo.Transferencia;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ModeloTest {

    private static final String TARJETA_VALIDA = "4509953566233704";

    private Curso cursoConTresLecciones() throws Exception {
        Curso curso = new Curso("PRG-101", "Java desde cero", new BigDecimal("45000"),
                new Academia("Code Academy SRL", "30-71234567-1"));
        curso.agregarLeccion(new LeccionVideo(1, "Que es la JVM", 1, 1080));
        curso.agregarLeccion(new LeccionTexto(2, "Estructuras de control", 2, 4000));
        curso.agregarLeccion(new LeccionCuestionario(3, "Autoevaluacion", 3, 10));
        return curso;
    }

    @Test
    void cadaTipoDeLeccionCalculaSuDuracion() throws Exception {
        Leccion[] lecciones = {
                new LeccionVideo(1, "Video", 1, 125),
                new LeccionTexto(2, "Texto", 2, 450),
                new LeccionCuestionario(3, "Cuestionario", 3, 10)
        };
        int[] esperado = {3, 3, 20};
        for (int i = 0; i < lecciones.length; i++) {
            assertEquals(esperado[i], lecciones[i].duracionEstimadaMinutos());
        }
        assertEquals(18 + 20 + 20, cursoConTresLecciones().getDuracionTotalMinutos());
    }

    @Test
    void cadaMedioDePagoCalculaSuImporte() throws Exception {
        BigDecimal precio = new BigDecimal("10000");
        MedioPago[] medios = {
                new TarjetaCredito("Ana Lopez", TARJETA_VALIDA, 1),
                new TarjetaCredito("Ana Lopez", TARJETA_VALIDA, 6),
                new TarjetaDebito("Ana Lopez", TARJETA_VALIDA),
                new Transferencia("Ana Lopez", "0170099220000012345678")
        };
        String[] esperado = {"10000.00", "12000.00", "10000.00", "9500.00"};
        for (int i = 0; i < medios.length; i++) {
            assertEquals(new BigDecimal(esperado[i]), medios[i].calcularImporteFinal(precio));
        }
    }

    @Test
    void lasTarjetasDePruebaSeRechazan() throws Exception {
        assertDoesNotThrow(() -> new TarjetaDebito("Ana Lopez", TARJETA_VALIDA).autorizar());
        PagoRechazadoException fondos = assertThrows(PagoRechazadoException.class,
                () -> new TarjetaCredito("Ana Lopez", "4509953566230000", 1).autorizar());
        assertEquals("fondos insuficientes", fondos.getMotivo());
        assertThrows(PagoRechazadoException.class, () -> new TarjetaDebito("Ana Lopez", "4509953566231111").autorizar());
    }

    @Test
    void losDatosInvalidosSeRechazanAlConstruir() {
        assertThrows(ValidacionException.class, () -> new Academia("Academia", "30-71234567-9"));
        assertThrows(ValidacionException.class, () -> new Alumno("Ana", "Lopez", "ana.gmail.com"));
        assertThrows(ValidacionException.class, () -> new TarjetaCredito("Ana", "4509953566233705", 1));
        assertThrows(ValidacionException.class, () -> new TarjetaCredito("Ana", TARJETA_VALIDA, 2));
        assertThrows(ValidacionException.class, () -> new Transferencia("Ana", "123"));
    }

    @Test
    void noSePuedePublicarUnCursoSinLecciones() throws Exception {
        Curso vacio = new Curso("GES-130", "Git", new BigDecimal("28000"),
                new Academia("Code Academy SRL", "30-71234567-1"));
        assertThrows(ReglaNegocioException.class, vacio::publicar);
    }

    @Test
    void elAvanceSeApilaYSePuedeDeshacer() throws Exception {
        Curso curso = cursoConTresLecciones();
        Inscripcion inscripcion = new Inscripcion(new Alumno("Ana", "Lopez", "ana.lopez@gmail.com"), curso);
        assertThrows(ReglaNegocioException.class, () -> inscripcion.completarLeccion(curso.buscarLeccionPorOrden(1)));

        inscripcion.activar();
        inscripcion.completarLeccion(curso.buscarLeccionPorOrden(1));
        inscripcion.completarLeccion(curso.buscarLeccionPorOrden(3));
        assertThrows(ReglaNegocioException.class, () -> inscripcion.completarLeccion(curso.buscarLeccionPorOrden(1)));
        inscripcion.completarLeccion(curso.buscarLeccionPorOrden(2));
        assertEquals(EstadoInscripcion.COMPLETADA, inscripcion.getEstado());
        assertEquals(100, inscripcion.porcentajeAvance());

        assertEquals(2, inscripcion.deshacerUltimaLeccion().getOrden());
        assertEquals(EstadoInscripcion.ACTIVA, inscripcion.getEstado());
        assertEquals(3, inscripcion.getUltimaCompletada().getOrden());
        assertEquals(66, inscripcion.porcentajeAvance());
    }
}
