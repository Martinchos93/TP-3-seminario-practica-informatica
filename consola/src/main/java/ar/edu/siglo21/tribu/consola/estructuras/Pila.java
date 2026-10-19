package ar.edu.siglo21.tribu.consola.estructuras;

import ar.edu.siglo21.tribu.consola.excepciones.EstructuraVaciaException;

public class Pila<T> {

    private Nodo<T> tope;
    private int tamanio;

    public Pila() {
        tope = null;
        tamanio = 0;
    }

    public void apilar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        nuevo.setSiguiente(tope);
        tope = nuevo;
        tamanio++;
    }

    public T desapilar() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La pila está vacía");
        }
        T dato = tope.getDato();
        tope = tope.getSiguiente();
        tamanio--;
        return dato;
    }

    public T verTope() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La pila está vacía");
        }
        return tope.getDato();
    }

    public boolean estaVacia() {
        return tope == null;
    }

    public int tamanio() {
        return tamanio;
    }
}
