package modelo;

// Esta es la versión simple, ANTES de añadir 'numCartas'
public class Movimiento {
    final Carta cartaMovida;
    final TipoLugar origenTipo;
    final int origenIndice;
    final TipoLugar destinoTipo;
    final int destinoIndice;

    // Se usa el constructor de 5 parámetros
    public Movimiento(Carta carta, TipoLugar oTipo, int oIndice, TipoLugar dTipo, int dIndice) {
        this.cartaMovida = carta;
        this.origenTipo = oTipo;
        this.origenIndice = oIndice;
        this.destinoTipo = dTipo;
        this.destinoIndice = dIndice;
    }

    // Getters
    public Carta getCartaMovida() { return cartaMovida; }
    public TipoLugar getOrigenTipo() { return origenTipo; }
    public int getOrigenIndice() { return origenIndice; }
    public TipoLugar getDestinoTipo() { return destinoTipo; }
    public int getDestinoIndice() { return destinoIndice; }
}