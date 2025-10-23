package modelo;

import java.util.Objects;

public class Carta implements Comparable<Carta> {
    private final Rango rango;
    private final Palo palo;

    public Carta(Rango rango, Palo palo) {
        this.rango = rango;
        this.palo = palo;
    }

    public Rango getRango() { return rango; }
    public Palo getPalo() { return palo; }
    public Palo.Color getColor() { return palo.getColor(); }

    @Override
    public int compareTo(Carta otra) {
        return Integer.compare(this.rango.getValor(), otra.rango.getValor());
    }

    // --- MÉTODOS DE LÓGICA DEL JUEGO ---

    /**
     * Verifica si esta carta puede ir sobre 'otra' en el tableau (Regla de Eight Off).
     * (Mismo palo, un rango menor)
     */
    public boolean esAnteriorMismoPalo(Carta otra) {
        if (otra == null) return false;

        return this.palo == otra.palo &&
                this.rango.getValor() == otra.rango.getValor() - 1;
    }

    /**
     * Verifica si esta carta puede ir sobre 'otra' en la fundación.
     * (Mismo palo, un rango mayor)
     */
    public boolean esSiguienteMismoPalo(Carta otra) {
        if (otra == null && this.rango == Rango.AS) {
            return true;
        }
        return otra != null &&
                this.palo == otra.palo &&
                this.rango.getValor() == otra.rango.getValor() + 1;
    }


    @Override
    public String toString() {
        return rango.getDisplay() + palo.getSimbolo();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Carta carta = (Carta) o;
        return rango == carta.rango && palo == carta.palo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(rango, palo);
    }
}