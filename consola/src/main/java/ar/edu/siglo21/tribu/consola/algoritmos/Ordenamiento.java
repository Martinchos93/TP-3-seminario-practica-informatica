package ar.edu.siglo21.tribu.consola.algoritmos;

import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;

import java.util.Comparator;

public final class Ordenamiento {

    private Ordenamiento() {
    }

    public static <T> ListaEnlazada<T> quickSort(ListaEnlazada<T> lista, Comparator<? super T> criterio) {
        Object[] elementos = new Object[lista.tamanio()];
        int i = 0;
        for (T elemento : lista) {
            elementos[i++] = elemento;
        }
        quickSort(elementos, 0, elementos.length - 1, criterio);
        ListaEnlazada<T> ordenada = new ListaEnlazada<>();
        for (Object elemento : elementos) {
            ordenada.agregar(convertir(elemento));
        }
        return ordenada;
    }

    private static <T> void quickSort(Object[] elementos, int inicio, int fin, Comparator<? super T> criterio) {
        if (inicio >= fin) {
            return;
        }
        int posicionPivote = particionar(elementos, inicio, fin, criterio);
        quickSort(elementos, inicio, posicionPivote - 1, criterio);
        quickSort(elementos, posicionPivote + 1, fin, criterio);
    }

    private static <T> int particionar(Object[] elementos, int inicio, int fin, Comparator<? super T> criterio) {
        T pivote = convertir(elementos[fin]);
        int menor = inicio - 1;
        for (int j = inicio; j < fin; j++) {
            if (criterio.compare(convertir(elementos[j]), pivote) <= 0) {
                menor++;
                intercambiar(elementos, menor, j);
            }
        }
        intercambiar(elementos, menor + 1, fin);
        return menor + 1;
    }

    private static void intercambiar(Object[] elementos, int a, int b) {
        Object auxiliar = elementos[a];
        elementos[a] = elementos[b];
        elementos[b] = auxiliar;
    }

    @SuppressWarnings("unchecked")
    private static <T> T convertir(Object elemento) {
        return (T) elemento;
    }
}
