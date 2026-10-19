package ar.edu.siglo21.tribu.consola.estructuras;

import ar.edu.siglo21.tribu.consola.excepciones.EstructuraVaciaException;

public class Cola<T> {

    private Nodo<T> frente;
    private Nodo<T> fondo;
    private int tamanio;

    public Cola() {
        frente = null;
        fondo = null;
        tamanio = 0;
    }

    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (estaVacia()) {
            frente = nuevo;
        } else {
            fondo.setSiguiente(nuevo);
        }
        fondo = nuevo;
        tamanio++;
    }

    public T desencolar() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola está vacía");
        }
        T dato = frente.getDato();
        frente = frente.getSiguiente();
        if (frente == null) {
            fondo = null;
        }
        tamanio--;
        return dato;
    }

    public T verFrente() {
        if (estaVacia()) {
            throw new EstructuraVaciaException("La cola está vacía");
        }
        return frente.getDato();
    }

    public boolean estaVacia() {
        return frente == null;
    }

    public int tamanio() {
        return tamanio;
    }
}
