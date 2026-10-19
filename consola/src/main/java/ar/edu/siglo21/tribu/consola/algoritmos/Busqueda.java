package ar.edu.siglo21.tribu.consola.algoritmos;

import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;

import java.util.function.Function;
import java.util.function.Predicate;

public final class Busqueda {

    private Busqueda() {
    }

    public static <T, K extends Comparable<K>> T binaria(ListaEnlazada<T> ordenada, K clave, Function<T, K> extractor) {
        Object[] elementos = new Object[ordenada.tamanio()];
        int i = 0;
        for (T elemento : ordenada) {
            elementos[i++] = elemento;
        }
        int izquierda = 0;
        int derecha = elementos.length - 1;
        while (izquierda <= derecha) {
            int medio = (izquierda + derecha) / 2;
            @SuppressWarnings("unchecked")
            T candidato = (T) elementos[medio];
            int comparacion = extractor.apply(candidato).compareTo(clave);
            if (comparacion == 0) {
                return candidato;
            } else if (comparacion < 0) {
                izquierda = medio + 1;
            } else {
                derecha = medio - 1;
            }
        }
        return null;
    }

    public static <T> ListaEnlazada<T> secuencial(ListaEnlazada<T> lista, Predicate<T> condicion) {
        ListaEnlazada<T> encontrados = new ListaEnlazada<>();
        for (T elemento : lista) {
            if (condicion.test(elemento)) {
                encontrados.agregar(elemento);
            }
        }
        return encontrados;
    }
}
