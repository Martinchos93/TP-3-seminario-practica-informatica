package ar.edu.siglo21.tribu.consola;

import ar.edu.siglo21.tribu.consola.estructuras.Cola;
import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;
import ar.edu.siglo21.tribu.consola.estructuras.Pila;
import ar.edu.siglo21.tribu.consola.excepciones.EstructuraVaciaException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EstructurasTest {

    @Test
    void laListaMantieneElOrdenYPermiteEliminar() {
        ListaEnlazada<String> lista = new ListaEnlazada<>();
        lista.agregar("b");
        lista.agregar("c");
        lista.agregarAlInicio("a");
        assertEquals(3, lista.tamanio());
        assertEquals("a", lista.obtener(0));
        assertEquals("c", lista.eliminar(2));
        lista.agregar("d");
        assertEquals("d", lista.obtener(2));
        assertEquals("a", lista.eliminar(0));
        assertTrue(lista.contiene("b"));
        assertThrows(IndexOutOfBoundsException.class, () -> lista.obtener(5));
    }

    @Test
    void laPilaDevuelveElUltimoQueEntro() {
        Pila<Integer> pila = new Pila<>();
        pila.apilar(1);
        pila.apilar(2);
        pila.apilar(3);
        assertEquals(3, pila.desapilar());
        assertEquals(2, pila.verTope());
        assertEquals(2, pila.tamanio());
    }

    @Test
    void laColaDevuelveElPrimeroQueEntro() {
        Cola<String> cola = new Cola<>();
        cola.encolar("primero");
        cola.encolar("segundo");
        assertEquals("primero", cola.desencolar());
        assertEquals("segundo", cola.desencolar());
        assertTrue(cola.estaVacia());
        cola.encolar("tercero");
        assertEquals("tercero", cola.verFrente());
    }

    @Test
    void sacarDeUnaEstructuraVaciaLanzaExcepcion() {
        assertThrows(EstructuraVaciaException.class, () -> new Pila<String>().desapilar());
        assertThrows(EstructuraVaciaException.class, () -> new Cola<String>().desencolar());
    }
}
