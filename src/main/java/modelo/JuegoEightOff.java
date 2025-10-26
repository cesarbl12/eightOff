package modelo;

import modelo.estructuras.ListaSimple;
import modelo.estructuras.ListaDoble;
import java.util.List;
import java.util.ArrayList;

public class JuegoEightOff {

    // --- Constantes ---
    public static final int NUM_TABLEAUS = 8;
    public static final int NUM_CELDAS = 8;
    public static final int NUM_FUNDACIONES = 4;

    // --- Atributos ---
    private ListaSimple<Carta>[] tableau;
    private Carta[] celdasReserva;
    private ListaSimple<Carta>[] fundaciones;
    private Baraja baraja;
    private ListaDoble<Movimiento> historial;

    // --- Constructor ---
    public JuegoEightOff() {
        tableau = new ListaSimple[NUM_TABLEAUS];
        for (int i = 0; i < NUM_TABLEAUS; i++) tableau[i] = new ListaSimple<>();
        celdasReserva = new Carta[NUM_CELDAS];
        fundaciones = new ListaSimple[NUM_FUNDACIONES];
        for (int i = 0; i < NUM_FUNDACIONES; i++) fundaciones[i] = new ListaSimple<>();
        historial = new ListaDoble<>();
        baraja = new Baraja();
        iniciarJuego();
    }

    // --- Inicialización ---
    public void iniciarJuego() {
        baraja.reiniciar();
        baraja.barajar();
        for (int i = 0; i < NUM_TABLEAUS; i++) tableau[i] = new ListaSimple<>();
        for (int i = 0; i < NUM_FUNDACIONES; i++) fundaciones[i] = new ListaSimple<>();
        for (int i = 0; i < NUM_CELDAS; i++) celdasReserva[i] = null;
        historial = new ListaDoble<>(); // Reiniciar historial

        // Reparto
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

    // --- Lógica de Supermove ---
    private int getCeldasVacias() {
        int contador = 0;
        for (Carta c : celdasReserva) {
            if (c == null) contador++;
        }
        return contador;
    }

    private int getTableausVacios(int dIndiceDestino) {
        int contador = 0;
        for (int i = 0; i < NUM_TABLEAUS; i++) {
            if (i == dIndiceDestino) continue;
            if (tableau[i].estaVacia()) contador++;
        }
        return contador;
    }

    private int getCapacidadMaxima(TipoLugar dTipo, int dIndice) {
        int c = getCeldasVacias();
        int t = (dTipo == TipoLugar.TABLEAU) ? getTableausVacios(dIndice) : getTableausVacios(-1);
        return (c + 1) * (1 << t); // (c+1) * 2^t
    }

    // --- Lógica de Movimiento ---
    public boolean intentarMoverSecuencia(TipoLugar oTipo, int oIndice, TipoLugar dTipo, int dIndice, int N) {
        if (N == 1) { // Caso simple N=1
            Carta c = verCarta(oTipo, oIndice);
            if (!esMovimientoValido(c, dTipo, dIndice)) return false;
            c = sacarCarta(oTipo, oIndice);
            ponerCarta(c, dTipo, dIndice);
            historial.agregar(new Movimiento(c, oTipo, oIndice, dTipo, dIndice, 1));
            return true;
        }

        // Caso Supermove N>1
        if (oTipo != TipoLugar.TABLEAU) return false;
        int capacidad = getCapacidadMaxima(dTipo, dIndice);
        if (N > capacidad) return false;

        ListaSimple<Carta> secuencia = sacarSecuencia(oTipo, oIndice, N);
        if (secuencia == null) return false; // Secuencia inválida

        // Invertir temporalmente para obtener la carta base
        ListaSimple<Carta> pilaInvertida = new ListaSimple<>();
        Carta cartaBase = null;
        while (!secuencia.estaVacia()) {
            cartaBase = secuencia.pop();
            pilaInvertida.push(cartaBase);
        }

        if (!esMovimientoValido(cartaBase, dTipo, dIndice)) {
            // Devolver secuencia si el destino no es válido
            while(!pilaInvertida.estaVacia()) secuencia.push(pilaInvertida.pop());
            ponerSecuencia(secuencia, oTipo, oIndice);
            return false;
        }

        // Éxito: Poner secuencia (invertida de nuevo) y guardar historial
        while(!pilaInvertida.estaVacia()) secuencia.push(pilaInvertida.pop());
        ponerSecuencia(secuencia, dTipo, dIndice);
        historial.agregar(new Movimiento(cartaBase, oTipo, oIndice, dTipo, dIndice, N));
        return true;
    }

    public boolean intentarMover(TipoLugar origenTipo, int origenIndice, TipoLugar destinoTipo, int destinoIndice) {
        return intentarMoverSecuencia(origenTipo, origenIndice, destinoTipo, destinoIndice, 1);
    }

    private ListaSimple<Carta> sacarSecuencia(TipoLugar oTipo, int oIndice, int N) {
        if (oTipo == TipoLugar.CELDA_RESERVA) {
            if (N != 1 || celdasReserva[oIndice] == null) return null;
            ListaSimple<Carta> pilaTemp = new ListaSimple<>();
            pilaTemp.push(celdasReserva[oIndice]);
            celdasReserva[oIndice] = null;
            return pilaTemp;
        }

        ListaSimple<Carta> pilaOrigen = tableau[oIndice];
        if (pilaOrigen.getTamano() < N) return null;
        ListaSimple<Carta> pilaTemp = new ListaSimple<>();
        Carta cartaAnterior = null;

        for (int i = 0; i < N; i++) {
            Carta cartaActual = pilaOrigen.pop();
            if (cartaAnterior != null && !cartaAnterior.esAnteriorMismoPalo(cartaActual)) {
                pilaTemp.push(cartaActual);
                while (!pilaTemp.estaVacia()) pilaOrigen.push(pilaTemp.pop());
                return null;
            }
            pilaTemp.push(cartaActual);
            cartaAnterior = cartaActual;
        }
        // Devuelve [Base, ..., Cima] (ej. [9, 8] con 8 en tope)
        return pilaTemp;
    }

    private void ponerSecuencia(ListaSimple<Carta> secuencia, TipoLugar dTipo, int dIndice) {
        if (dTipo == TipoLugar.CELDA_RESERVA) {
            celdasReserva[dIndice] = secuencia.pop();
            return;
        }
        ListaSimple<Carta> pilaDestino = (dTipo == TipoLugar.TABLEAU) ? tableau[dIndice] : fundaciones[dIndice];
        // Invertir la pila [Base,...,Cima] a [Cima,...,Base] para ponerla
        ListaSimple<Carta> pilaInvertida = new ListaSimple<>();
        while (!secuencia.estaVacia()) {
            pilaInvertida.push(secuencia.pop());
        }
        // Poner en destino
        while (!pilaInvertida.estaVacia()) {
            pilaDestino.push(pilaInvertida.pop());
        }
    }

    private Carta sacarCarta(TipoLugar tipo, int indice) {
        switch (tipo) {
            case TABLEAU: return tableau[indice].pop();
            case CELDA_RESERVA:
                Carta c = celdasReserva[indice];
                celdasReserva[indice] = null;
                return c;
            case FUNDACION: return fundaciones[indice].pop();
        }
        return null;
    }

    private void ponerCarta(Carta carta, TipoLugar tipo, int indice) {
        if (carta == null) return;
        switch (tipo) {
            case TABLEAU: tableau[indice].push(carta); break;
            case CELDA_RESERVA: celdasReserva[indice] = carta; break;
            case FUNDACION: fundaciones[indice].push(carta); break;
        }
    }

    public boolean esMovimientoValido(Carta cartaMovida, TipoLugar dTipo, int dIndice) {
        if (cartaMovida == null) return false;
        Carta cartaDestino = verCarta(dTipo, dIndice);
        switch (dTipo) {
            case TABLEAU:
                if (cartaDestino == null) return true; // Columna vacía
                return cartaMovida.esAnteriorMismoPalo(cartaDestino);
            case FUNDACION:
                Palo paloRequerido = getPaloFundacion(dIndice);
                if (cartaMovida.getPalo() != paloRequerido) return false;
                return cartaMovida.esSiguienteMismoPalo(cartaDestino);
            case CELDA_RESERVA:
                return cartaDestino == null; // Celda vacía
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
        if (index >= 0 && index < Palo.values().length) {
            return Palo.values()[index];
        }
        return Palo.PICAS; // Default
    }

    // --- Funciones de Historial ---
    public boolean undoMovimiento() {
        Movimiento movActual = historial.getActual();
        if (movActual == null) return false;
        if (!historial.retroceder()) return false;

        // Invertir N=1
        if (movActual.getNumCartas() == 1) {
            Carta c = sacarCarta(movActual.getDestinoTipo(), movActual.getDestinoIndice());
            ponerCarta(c, movActual.getOrigenTipo(), movActual.getOrigenIndice());
            return true;
        }
        // Invertir N>1
        ListaSimple<Carta> secuencia = sacarSecuencia(
                movActual.getDestinoTipo(),
                movActual.getDestinoIndice(),
                movActual.getNumCartas()
        );
        if (secuencia != null) {
            ponerSecuencia(secuencia, movActual.getOrigenTipo(), movActual.getOrigenIndice());
            return true;
        }
        historial.avanzar(); // Revertir retroceso si falló
        return false;
    }

    public boolean redoMovimiento() {
        Movimiento movSiguiente = historial.getSiguiente();
        if (movSiguiente == null) return false;
        if (!historial.avanzar()) return false;

        // Rehacer N=1
        if (movSiguiente.getNumCartas() == 1) {
            Carta c = sacarCarta(movSiguiente.getOrigenTipo(), movSiguiente.getOrigenIndice());
            ponerCarta(c, movSiguiente.getDestinoTipo(), movSiguiente.getDestinoIndice());
            return true;
        }
        // Rehacer N>1
        ListaSimple<Carta> secuencia = sacarSecuencia(
                movSiguiente.getOrigenTipo(),
                movSiguiente.getOrigenIndice(),
                movSiguiente.getNumCartas()
        );
        if (secuencia != null) {
            ponerSecuencia(secuencia, movSiguiente.getDestinoTipo(), movSiguiente.getDestinoIndice());
            return true;
        }
        historial.retroceder(); // Revertir avance si falló
        return false;
    }

    public void irAlInicioHistorial() {
        while(undoMovimiento()) {}
    }

    public void irAlFinalHistorial() {
        while(redoMovimiento()) {}
    }

    /**
     * Confirma el estado actual del historial, eliminando todos los
     * movimientos futuros (REDO).
     */
    public void aplicarEstadoActualHistorial() {
        // Llama al método correspondiente de tu ListaDoble
        historial.truncarDesdeCursor();
    }

    public List<Movimiento> getHistorialCompleto() {
        return historial.getTodosLosElementos();
    }

    public int getPosicionCursorHistorial() {
        return historial.getPosicionCursor();
    }

    public Movimiento getMovimientoEnCursor() {
        return historial.getActual();
    }

    public boolean estaEnInicio() {
        return historial.estaEnInicio();
    }

    public boolean estaEnFinal() {
        return historial.estaEnFinal();
    }

    // --- Validación y Pistas ---
    private boolean esSecuenciaValida(int oIndice, int N) {
        if (N <= 1) return true;
        ListaSimple<Carta> columna = tableau[oIndice];
        if (columna.getTamano() < N) return false;
        List<Carta> cartas = new ArrayList<>(columna.getComoListaJava());
        // Asumiendo [FONDO...TOPE]
        int indiceBase = cartas.size() - N;
        for (int i = indiceBase; i < cartas.size() - 1; i++) {
            Carta actual = cartas.get(i);
            Carta siguiente = cartas.get(i+1);
            if (!siguiente.esAnteriorMismoPalo(actual)) {
                return false;
            }
        }
        return true;
    }

    public Movimiento buscarPista() {
        // Prioridad 1: Celdas a Fundación
        for (int i = 0; i < NUM_CELDAS; i++) {
            Carta c = verCarta(TipoLugar.CELDA_RESERVA, i);
            if (c != null) {
                for (int f = 0; f < NUM_FUNDACIONES; f++) {
                    if (esMovimientoValido(c, TipoLugar.FUNDACION, f))
                        return new Movimiento(c, TipoLugar.CELDA_RESERVA, i, TipoLugar.FUNDACION, f, 1);
                }
            }
        }
        // Prioridad 2: Tableau a Fundación
        for (int i = 0; i < NUM_TABLEAUS; i++) {
            Carta c = verCarta(TipoLugar.TABLEAU, i);
            if (c != null) {
                for (int f = 0; f < NUM_FUNDACIONES; f++) {
                    if (esMovimientoValido(c, TipoLugar.FUNDACION, f))
                        return new Movimiento(c, TipoLugar.TABLEAU, i, TipoLugar.FUNDACION, f, 1);
                }
            }
        }
        // Prioridad 3: Descubrir en Tableau (a Tableau o Celda)
        for (int tOrigen = 0; tOrigen < NUM_TABLEAUS; tOrigen++) {
            ListaSimple<Carta> colOrigen = tableau[tOrigen];
            if (colOrigen.estaVacia()) continue;
            List<Carta> cartasOrigen = colOrigen.getComoListaJava();
            for (int i = cartasOrigen.size() - 1; i >= 0; i--) { // Desde tope hacia abajo
                int N = cartasOrigen.size() - i;
                if (N >= colOrigen.getTamano()) continue; // Solo si descubre
                Carta cartaBase = cartasOrigen.get(i);
                if (!esSecuenciaValida(tOrigen, N)) continue;
                int capacidadMax = (getCeldasVacias() + 1) * (1 << (getTableausVacios(-1)));
                if (N > capacidadMax) continue;
                // A Tableau
                for (int tDestino = 0; tDestino < NUM_TABLEAUS; tDestino++) {
                    if (tOrigen == tDestino) continue;
                    int capacidadReal = getCapacidadMaxima(TipoLugar.TABLEAU, tDestino);
                    if (N <= capacidadReal && esMovimientoValido(cartaBase, TipoLugar.TABLEAU, tDestino)) {
                        return new Movimiento(cartaBase, TipoLugar.TABLEAU, tOrigen, TipoLugar.TABLEAU, tDestino, N);
                    }
                }
                // A Celda (solo N=1)
                if (N == 1) {
                    for (int cDestino = 0; cDestino < NUM_CELDAS; cDestino++) {
                        if (esMovimientoValido(cartaBase, TipoLugar.CELDA_RESERVA, cDestino)) {
                            return new Movimiento(cartaBase, TipoLugar.TABLEAU, tOrigen, TipoLugar.CELDA_RESERVA, cDestino, 1);
                        }
                    }
                }
            }
        }
        // Prioridad 4: Liberar Celdas
        for (int i = 0; i < NUM_CELDAS; i++) {
            Carta c = verCarta(TipoLugar.CELDA_RESERVA, i);
            if (c != null) {
                for (int t = 0; t < NUM_TABLEAUS; t++) {
                    if (esMovimientoValido(c, TipoLugar.TABLEAU, t))
                        return new Movimiento(c, TipoLugar.CELDA_RESERVA, i, TipoLugar.TABLEAU, t, 1);
                }
            }
        }
        // Prioridad 5: Mover Secuencias (sin descubrir)
        for (int tOrigen = 0; tOrigen < NUM_TABLEAUS; tOrigen++) {
            if (tableau[tOrigen].estaVacia()) continue;
            List<Carta> cartasOrigen = tableau[tOrigen].getComoListaJava();
            for (int i = 0; i < cartasOrigen.size(); i++) { // Desde base hasta tope
                int N = cartasOrigen.size() - i;
                Carta cartaBase = cartasOrigen.get(i);
                if (!esSecuenciaValida(tOrigen, N)) continue;
                for (int tDestino = 0; tDestino < NUM_TABLEAUS; tDestino++) {
                    if (tOrigen == tDestino) continue;
                    int capacidad = getCapacidadMaxima(TipoLugar.TABLEAU, tDestino);
                    if (N <= capacidad && esMovimientoValido(cartaBase, TipoLugar.TABLEAU, tDestino)) {
                        return new Movimiento(cartaBase, TipoLugar.TABLEAU, tOrigen, TipoLugar.TABLEAU, tDestino, N);
                    }
                }
            }
        }
        // Prioridad 6: Usar Celdas (solo si es la única carta)
        for (int i = 0; i < NUM_TABLEAUS; i++) {
            Carta c = verCarta(TipoLugar.TABLEAU, i);
            if (c != null && tableau[i].getTamano() == 1) { // Solo si N=1 y es la única
                for (int ce = 0; ce < NUM_CELDAS; ce++) {
                    if (esMovimientoValido(c, TipoLugar.CELDA_RESERVA, ce))
                        return new Movimiento(c, TipoLugar.TABLEAU, i, TipoLugar.CELDA_RESERVA, ce, 1);
                }
            }
        }
        return null; // No hay movimientos
    }

    public boolean verificarVictoria() {
        for (int i = 0; i < NUM_FUNDACIONES; i++) {
            if (fundaciones[i] == null || fundaciones[i].getTamano() != 13) return false;
        }
        return true;
    }

    public boolean verificarBloqueo() {
        return buscarPista() == null && !verificarVictoria();
    }

    // --- Getters ---
    public ListaSimple<Carta>[] getTableau() { return tableau; }
    public Carta[] getCeldasReserva() { return celdasReserva; }
    public ListaSimple<Carta>[] getFundaciones() { return fundaciones; }
}