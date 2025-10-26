package modelo;

public class Movimiento {
    final Carta cartaMovida;
    final TipoLugar origenTipo;
    final int origenIndice;
    final TipoLugar destinoTipo;
    final int destinoIndice;
    final int numCartas;

    public Movimiento(Carta carta, TipoLugar oTipo, int oIndice, TipoLugar dTipo, int dIndice, int numCartas) {
        this.cartaMovida = carta;
        this.origenTipo = oTipo;
        this.origenIndice = oIndice;
        this.destinoTipo = dTipo;
        this.destinoIndice = dIndice;
        this.numCartas = numCartas;
    }

    public Carta getCartaMovida() { return cartaMovida; }
    public TipoLugar getOrigenTipo() { return origenTipo; }
    public int getOrigenIndice() { return origenIndice; }
    public TipoLugar getDestinoTipo() { return destinoTipo; }
    public int getDestinoIndice() { return destinoIndice; }
    public int getNumCartas() { return numCartas; }
}
