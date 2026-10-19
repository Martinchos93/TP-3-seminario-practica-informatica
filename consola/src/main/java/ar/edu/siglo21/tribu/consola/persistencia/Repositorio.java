package ar.edu.siglo21.tribu.consola.persistencia;

import ar.edu.siglo21.tribu.consola.estructuras.ListaEnlazada;

public interface Repositorio<T> {

    ListaEnlazada<T> listarTodos();

    void guardar(T entidad);
}
