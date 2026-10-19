package ar.edu.siglo21.tribu.consola.estructuras;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class ListaEnlazada<T> implements Iterable<T> {

    private Nodo<T> primero;
    private Nodo<T> ultimo;
    private int tamanio;

    public ListaEnlazada() {
        primero = null;
        ultimo = null;
        tamanio = 0;
    }

    public void agregar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            primero = nuevo;
        } else {
            ultimo.setSiguiente(nuevo);
        }
        ultimo = nuevo;
        tamanio++;
    }

    public void agregarAlInicio(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(primero);
        primero = nuevo;
        if (ultimo == null) {
            ultimo = nuevo;
        }
        tamanio++;
    }

    public T obtener(int indice) {
        validarIndice(indice);
        Nodo<T> actual = primero;
        for (int i = 0; i < indice; i++) {
            actual = actual.getSiguiente();
        }
        return actual.getDato();
    }

    public T eliminar(int indice) {
        validarIndice(indice);
        Nodo<T> eliminado;
        if (indice == 0) {
            eliminado = primero;
            primero = primero.getSiguiente();
            if (primero == null) {
                ultimo = null;
            }
        } else {
            Nodo<T> anterior = primero;
            for (int i = 0; i < indice - 1; i++) {
                anterior = anterior.getSiguiente();
            }
            eliminado = anterior.getSiguiente();
            anterior.setSiguiente(eliminado.getSiguiente());
            if (eliminado == ultimo) {
                ultimo = anterior;
            }
        }
        tamanio--;
        return eliminado.getDato();
    }

    public boolean contiene(T dato) {
        for (T elemento : this) {
            if (elemento.equals(dato)) {
                return true;
            }
        }
        return false;
    }

    public int tamanio() {
        return tamanio;
    }

    public boolean estaVacia() {
        return tamanio == 0;
    }

    private void validarIndice(int indice) {
        if (indice < 0 || indice >= tamanio) {
            throw new IndexOutOfBoundsException("Índice " + indice + " fuera de rango (tamaño " + tamanio + ")");
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new Iterator<>() {
            private Nodo<T> actual = primero;

            @Override
            public boolean hasNext() {
                return actual != null;
            }

            @Override
            public T next() {
                if (actual == null) {
                    throw new NoSuchElementException();
                }
                T dato = actual.getDato();
                actual = actual.getSiguiente();
                return dato;
            }
        };
    }
}
