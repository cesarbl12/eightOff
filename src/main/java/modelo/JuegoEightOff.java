package modelo;

import modelo.estructuras.ListaSimple;

public class JuegoEightOff {

    public static final int NUM_TABLEAUS = 8;
    public static final int NUM_CELDAS = 8;
    public static final int NUM_FUNDACIONES = 4;

    private ListaSimple<Carta>[] tableau;
    private Carta[] celdasReserva;
    private ListaSimple<Carta>[] fundaciones;
    private Baraja baraja;
    private ListaSimple<Movimiento> historial;

    public JuegoEightOff() {
        tableau = new ListaSimple[NUM_TABLEAUS];
        for (int i = 0; i < NUM_TABLEAUS; i++) tableau[i] = new ListaSimple<>();
        celdasReserva = new Carta[NUM_CELDAS];
        fundaciones = new ListaSimple[NUM_FUNDACIONES];
        for (int i = 0; i < NUM_FUNDACIONES; i++) fundaciones[i] = new ListaSimple<>();
        historial = new ListaSimple<>();
        baraja = new Baraja();
        iniciarJuego();
    }

    public void iniciarJuego() {
        baraja.reiniciar();
        baraja.barajar();
        for (int i = 0; i < NUM_TABLEAUS; i++) tableau[i] = new ListaSimple<>();
        for (int i = 0; i < NUM_FUNDACIONES; i++) fundaciones[i] = new ListaSimple<>();
        for (int i = 0; i < NUM_CELDAS; i++) celdasReserva[i] = null;
        historial = new ListaSimple<>();

        for (int i = 0; i < NUM_TABLEAUS; i++) {
            for (int j = 0; j < 6; j++) {
                if (baraja.estaVacia()) break;
                tableau[i].push(baraja.repartir());
            }
        }
        for (int i = 0; i < 4; i++) {
            if (baraja.estaVacia()) break;
            celdasReserva[i] = baraja.repartir();
        }
    }

    // --- Lógica de Movimiento ---

    /**
     * Este es el método original (antes de Supermove)
     * Mueve SÓLO UNA carta.
     */
    public boolean intentarMover(TipoLugar oTipo, int oIndice, TipoLugar dTipo, int dIndice) {
        // Obtenemos la carta que se quiere mover (solo 1)
        Carta cartaMovida = verCarta(oTipo, oIndice);

        if (!esMovimientoValido(cartaMovida, dTipo, dIndice)) {
            return false;
        }

        // Realizar el movimiento
        cartaMovida = sacarCarta(oTipo, oIndice); // Sacar la carta
        ponerCarta(cartaMovida, dTipo, dIndice); // Ponerla en el destino

        historial.push(new Movimiento(cartaMovida, oTipo, oIndice, dTipo, dIndice));
        return true;
    }

    // Este método es el que solicitaste para compatibilidad
    public boolean intentarMoverSecuencia(TipoLugar oTipo, int oIndice, TipoLugar dTipo, int dIndice, int N) {
        if (N == 1) {
            return intentarMover(oTipo, oIndice, dTipo, dIndice);
        }
        // Esta versión no soporta Supermove (N > 1)
        return false;
    }


    private Carta sacarCarta(TipoLugar tipo, int indice) {
        switch (tipo) {
            case TABLEAU: return tableau[indice].pop();
            case CELDA_RESERVA:
                Carta c = celdasReserva[indice];
                celdasReserva[indice] = null;
                return c;
            case FUNDACION: return fundaciones[indice].pop(); // Usado solo en deshacer
        }
        return null;
    }

    private void ponerCarta(Carta carta, TipoLugar tipo, int indice) {
        switch (tipo) {
            case TABLEAU: tableau[indice].push(carta); break;
            case CELDA_RESERVA: celdasReserva[indice] = carta; break;
            case FUNDACION: fundaciones[indice].push(carta); break;
        }
    }

    /**
     * Valida si una CARTA específica puede ir a un DESTINO.
     */
    public boolean esMovimientoValido(Carta cartaMovida, TipoLugar dTipo, int dIndice) {
        if (cartaMovida == null) return false;
        Carta cartaDestino = verCarta(dTipo, dIndice);

        switch (dTipo) {
            case TABLEAU:
                if (cartaDestino == null) return true; // Se puede mover a col vacía
                return cartaMovida.esAnteriorMismoPalo(cartaDestino);

            case FUNDACION:
                Palo paloRequerido = getPaloFundacion(dIndice);
                if (cartaMovida.getPalo() != paloRequerido) return false;
                return cartaMovida.esSiguienteMismoPalo(cartaDestino);

            case CELDA_RESERVA:
                return cartaDestino == null;
        }
        return false;
    }

    private Carta verCarta(TipoLugar tipo, int indice) {
        switch (tipo) {
            case TABLEAU: return tableau[indice].peek();
            case CELDA_RESERVA: return celdasReserva[indice];
            case FUNDACION: return fundaciones[indice].peek();
        }
        return null;
    }

    private Palo getPaloFundacion(int index) {
        return Palo.values()[index];
    }

    // --- Funciones del Juego ---

    public boolean deshacerMovimiento() {
        if (historial.estaVacia()) return false;
        Movimiento ultimoMov = historial.pop();

        // Invertir el movimiento (solo 1 carta)
        Carta carta = sacarCarta(ultimoMov.getDestinoTipo(), ultimoMov.getDestinoIndice());
        ponerCarta(carta, ultimoMov.getOrigenTipo(), ultimoMov.getOrigenIndice());

        return true;
    }

    public Movimiento buscarPista() {
        // 1. Celdas a Fundación
        for (int i = 0; i < NUM_CELDAS; i++) {
            Carta c = verCarta(TipoLugar.CELDA_RESERVA, i);
            if (c == null) continue;
            for (int f = 0; f < NUM_FUNDACIONES; f++) {
                if (esMovimientoValido(c, TipoLugar.FUNDACION, f))
                    return new Movimiento(c, TipoLugar.CELDA_RESERVA, i, TipoLugar.FUNDACION, f);
            }
        }
        // 2. Tableau a Fundación
        for (int i = 0; i < NUM_TABLEAUS; i++) {
            Carta c = verCarta(TipoLugar.TABLEAU, i);
            if (c == null) continue;
            for (int f = 0; f < NUM_FUNDACIONES; f++) {
                if (esMovimientoValido(c, TipoLugar.FUNDACION, f))
                    return new Movimiento(c, TipoLugar.TABLEAU, i, TipoLugar.FUNDACION, f);
            }
        }
        // 3. Celdas a Tableau
        for (int i = 0; i < NUM_CELDAS; i++) {
            Carta c = verCarta(TipoLugar.CELDA_RESERVA, i);
            if (c == null) continue;
            for (int t = 0; t < NUM_TABLEAUS; t++) {
                if (esMovimientoValido(c, TipoLugar.TABLEAU, t))
                    return new Movimiento(c, TipoLugar.CELDA_RESERVA, i, TipoLugar.TABLEAU, t);
            }
        }
        // 4. Tableau a Tableau
        for (int i = 0; i < NUM_TABLEAUS; i++) {
            Carta c = verCarta(TipoLugar.TABLEAU, i);
            if (c == null) continue;
            for (int t = 0; t < NUM_TABLEAUS; t++) {
                if (i == t) continue;
                if (esMovimientoValido(c, TipoLugar.TABLEAU, t))
                    return new Movimiento(c, TipoLugar.TABLEAU, i, TipoLugar.TABLEAU, t);
            }
        }
        // 5. Tableau a Celda vacía
        for (int i = 0; i < NUM_TABLEAUS; i++) {
            Carta c = verCarta(TipoLugar.TABLEAU, i);
            if (c == null) continue;
            for (int ce = 0; ce < NUM_CELDAS; ce++) {
                if (esMovimientoValido(c, TipoLugar.CELDA_RESERVA, ce))
                    return new Movimiento(c, TipoLugar.TABLEAU, i, TipoLugar.CELDA_RESERVA, ce);
            }
        }
        return null; // No hay movimientos
    }

    public boolean verificarVictoria() {
        for (int i = 0; i < NUM_FUNDACIONES; i++) {
            if (fundaciones[i].getTamano() != 13) return false;
        }
        return true;
    }

    public boolean verificarBloqueo() {
        return buscarPista() == null && !verificarVictoria();
    }

    public ListaSimple<Carta>[] getTableau() { return tableau; }
    public Carta[] getCeldasReserva() { return celdasReserva; }
    public ListaSimple<Carta>[] getFundaciones() { return fundaciones; }
}