package modelo.estructuras;

import java.util.Collections;
import java.util.LinkedList; // ¡Quita esto al usar tu lista!

/**
 * ¡PLANTILLA! Reemplaza esta clase con tu propia implementación de ListaDobleCircular.
 */
public class ListaDobleCircular<T> {

    // Simulo tu lista con un LinkedList de Java.
    // ¡BORRA ESTO y usa tus propios nodos y lógica!
    private LinkedList<T> miListaSimulada;

    public ListaDobleCircular() {
        // ¡Inicializa tu lista aquí!
        this.miListaSimulada = new LinkedList<>();
    }

    // Método para agregar un elemento
    public void agregar(T dato) {
        // Implementa tu lógica de "agregar"
        miListaSimulada.add(dato);
    }

    // Método para barajar la lista
    public void barajar() {
        // Implementa tu propia lógica de barajado si quieres.
        // O usa Collections.shuffle si tu lista lo permite.
        Collections.shuffle(miListaSimulada);
    }

    // Método para sacar un elemento (para repartir)
    public T sacar() {
        // Implementa tu lógica de "eliminar/sacar"
        if (estaVacia()) return null;
        return miListaSimulada.pop();
    }

    public boolean estaVacia() {
        // Implementa tu lógica de "estaVacia"
        return miListaSimulada.isEmpty();
    }
}