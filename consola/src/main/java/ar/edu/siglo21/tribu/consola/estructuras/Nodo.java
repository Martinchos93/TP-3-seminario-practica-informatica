package ar.edu.siglo21.tribu.consola.estructuras;

class Nodo<T> {

    private final T dato;
    private Nodo<T> siguiente;

    Nodo(T dato) {
        this.dato = dato;
        this.siguiente = null;
    }

    T getDato() {
        return dato;
    }

    Nodo<T> getSiguiente() {
        return siguiente;
    }

    void setSiguiente(Nodo<T> siguiente) {
        this.siguiente = siguiente;
    }
}
