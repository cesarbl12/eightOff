package modelo.estructuras;

import java.util.Stack; // ¡Quita esto al usar tu lista!

/**
 * ¡PLANTILLA! Reemplaza esta clase con tu propia implementación de ListaSimple.
 * El juego espera una estructura tipo Pila (LIFO - Last In, First Out).
 */
public class ListaSimple<T> {

    // Simulo tu lista con un Stack de Java.
    // ¡BORRA ESTO y usa tus propios nodos y lógica!
    private Stack<T> miListaSimulada;

    public ListaSimple() {
        // ¡Inicializa tu lista aquí!
        this.miListaSimulada = new Stack<>();
    }

    // Método para agregar al inicio (como una pila)
    public void push(T dato) {
        // Implementa tu lógica de "agregar al inicio"
        miListaSimulada.push(dato);
    }

    // Método para quitar del inicio (como una pila)
    public T pop() {
        // Implementa tu lógica de "eliminar del inicio"
        if (estaVacia()) return null;
        return miListaSimulada.pop();
    }

    // Método para ver el último elemento (el del inicio)
    public T peek() {
        // Implementa tu lógica de "ver el primer elemento"
        if (estaVacia()) return null;
        return miListaSimulada.peek();
    }

    public boolean estaVacia() {
        // Implementa tu lógica de "estaVacia"
        return miListaSimulada.isEmpty();
    }

    public int getTamano() {
        // Implementa tu lógica de "tamano"
        return miListaSimulada.size();
    }

    // El controlador necesitará iterar o obtener elementos para la vista.
    // Si tu lista no es iterable, añade un método como este.
    public java.util.List<T> getComoListaJava() {
        return new java.util.ArrayList<>(miListaSimulada);
    }
}