package modelo.estructuras;

import java.util.Collections;
import java.util.LinkedList;

public class ListaDobleCircular<T> {

    private LinkedList<T> miListaSimulada;

    public ListaDobleCircular() {
        this.miListaSimulada = new LinkedList<>();
    }

    public void agregar(T dato) {
        miListaSimulada.add(dato);
    }

    public void barajar() {
        Collections.shuffle(miListaSimulada);
    }

    public T sacar() {
        if (estaVacia()) return null;
        return miListaSimulada.pop();
    }

    public boolean estaVacia() {
        return miListaSimulada.isEmpty();
    }
}
