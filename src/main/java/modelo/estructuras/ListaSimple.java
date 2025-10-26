package modelo.estructuras;

import java.util.Stack;

public class ListaSimple<T> {

    private Stack<T> miListaSimulada;

    public ListaSimple() {
        this.miListaSimulada = new Stack<>();
    }

    public void push(T dato) {
        miListaSimulada.push(dato);
    }

    public T pop() {
        if (estaVacia()) return null;
        return miListaSimulada.pop();
    }

    public T peek() {
        if (estaVacia()) return null;
        return miListaSimulada.peek();
    }

    public boolean estaVacia() {
        return miListaSimulada.isEmpty();
    }

    public int getTamano() {
        return miListaSimulada.size();
    }

    public java.util.List<T> getComoListaJava() {
        return new java.util.ArrayList<>(miListaSimulada);
    }
}
