package ar.edu.siglo21.tribu.consola;

import ar.edu.siglo21.tribu.consola.algoritmos.Busqueda;
import ar.edu.siglo21.tribu.consola.algoritmos.Ordenamiento;
import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class AlgoritmosTest {

    private ListaEnlazada<Integer> lista(int... valores) {
        ListaEnlazada<Integer> lista = new ListaEnlazada<>();
        for (int valor : valores) {
            lista.agregar(valor);
        }
        return lista;
    }

    @Test
    void quickSortOrdenaAscendenteYDescendente() {
        ListaEnlazada<Integer> ordenada = Ordenamiento.quickSort(lista(5, 3, 9, 1, 3, 7), Comparator.naturalOrder());
        int[] esperado = {1, 3, 3, 5, 7, 9};
        for (int i = 0; i < esperado.length; i++) {
            assertEquals(esperado[i], ordenada.obtener(i));
        }
        ListaEnlazada<Integer> inversa = Ordenamiento.quickSort(lista(2, 8, 4), Comparator.reverseOrder());
        assertEquals(8, inversa.obtener(0));
        assertEquals(2, inversa.obtener(2));
    }

    @Test
    void quickSortAceptaListasVaciasYDeUnElemento() {
        assertEquals(0, Ordenamiento.quickSort(lista(), Comparator.<Integer>naturalOrder()).tamanio());
        assertEquals(4, Ordenamiento.quickSort(lista(4), Comparator.<Integer>naturalOrder()).obtener(0));
    }

    @Test
    void busquedaBinariaEncuentraONoEncuentra() {
        ListaEnlazada<Integer> ordenada = lista(1, 4, 6, 10, 15, 21);
        assertEquals(15, Busqueda.binaria(ordenada, 15, Function.identity()));
        assertEquals(1, Busqueda.binaria(ordenada, 1, Function.identity()));
        assertNull(Busqueda.binaria(ordenada, 7, Function.identity()));
    }

    @Test
    void busquedaSecuencialFiltraPorCondicion() {
        ListaEnlazada<Integer> pares = Busqueda.secuencial(lista(1, 2, 3, 4, 5, 6), n -> n % 2 == 0);
        assertEquals(3, pares.tamanio());
        assertEquals(6, pares.obtener(2));
    }
}
