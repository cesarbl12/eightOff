package vista;

import javafx.geometry.Pos;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import modelo.Carta;
import modelo.TipoLugar;

public class CeldaVista extends StackPane {
    private TipoLugar tipo;
    private int indice;
    private Rectangle fondoVacio;

    public CeldaVista(TipoLugar tipo, int indice) {
        this.tipo = tipo;
        this.indice = indice;

        fondoVacio = new Rectangle(CartaVista.CARTA_ANCHO, CartaVista.CARTA_ALTO);
        fondoVacio.setFill(Color.BLACK.deriveColor(1, 1, 1, 0.1));
        fondoVacio.setStroke(Color.DARKGREEN);
        fondoVacio.setStrokeWidth(2);
        fondoVacio.setArcWidth(10);
        fondoVacio.setArcHeight(10);

        getChildren().add(fondoVacio);
        setAlignment(Pos.CENTER);
        setPrefSize(CartaVista.CARTA_ANCHO, CartaVista.CARTA_ALTO);
    }

    public void setCarta(Carta carta) {
        getChildren().removeIf(node -> node instanceof CartaVista);
        if (carta != null) {
            CartaVista cv = new CartaVista(carta);
            getChildren().add(cv);
        }
    }

    public CartaVista getCartaVista() {
        return getChildren().stream()
                .filter(n -> n instanceof CartaVista)
                .map(n -> (CartaVista) n)
                .findFirst()
                .orElse(null);
    }

    public TipoLugar getTipo() { return tipo; }
    public int getIndice() { return indice; }
}