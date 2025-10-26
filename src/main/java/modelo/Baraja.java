package modelo;

import modelo.estructuras.ListaDobleCircular;

public class Baraja {
    private ListaDobleCircular<Carta> cartas;

    public Baraja() {
        this.cartas = new ListaDobleCircular<>();
        reiniciar();
    }

    public void reiniciar() {
        cartas = new ListaDobleCircular<>();
        for (Palo palo : Palo.values()) {
            for (Rango rango : Rango.values()) {
                cartas.agregar(new Carta(rango, palo));
            }
        }
    }

    public void barajar() {
        cartas.barajar();
    }

    public Carta repartir() {
        return cartas.sacar();
    }

    public boolean estaVacia() {
        return cartas.estaVacia();
    }
}
