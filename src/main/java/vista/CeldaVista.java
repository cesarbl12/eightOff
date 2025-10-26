package vista;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import modelo.Carta;
import modelo.Palo; // Asegúrate que Palo está importado
import modelo.TipoLugar;

public class CeldaVista extends StackPane {
    private TipoLugar tipo;
    private int indice;
    private Rectangle fondoVacio;
    private Rectangle bordeDestino;
    private Label simboloPaloFondo;
    private Rectangle bordeHistorialOrigen;
    private Rectangle bordeHistorialDestino;

    public CeldaVista(TipoLugar tipo, int indice) {
        this.tipo = tipo;
        this.indice = indice;

        fondoVacio = new Rectangle(CartaVista.CARTA_ANCHO, CartaVista.CARTA_ALTO);
        fondoVacio.setFill(Color.BLACK.deriveColor(1, 1, 1, 0.1));
        fondoVacio.setStroke(Color.DARKGREEN);
        fondoVacio.setStrokeWidth(2);
        fondoVacio.setArcWidth(10);
        fondoVacio.setArcHeight(10);

        // --- Bordes ---
        bordeDestino = crearBorde(Color.LIMEGREEN, 3, true);
        bordeHistorialOrigen = crearBorde(Color.DEEPSKYBLUE, 2, false);
        bordeHistorialDestino = crearBorde(Color.MEDIUMPURPLE, 2, true);

        getChildren().addAll(fondoVacio, bordeDestino, bordeHistorialOrigen, bordeHistorialDestino);

        // --- Añadir Símbolo de Palo para Fundaciones ---
        if (tipo == TipoLugar.FUNDACION) {
            Palo paloAsignado = getPaloFundacion(indice);

            // Verificación extra (aunque getPaloFundacion no debería devolver null)
            if (paloAsignado != null) {
                simboloPaloFondo = new Label(paloAsignado.getSimbolo()); // <--- Línea 42 (aprox)
                simboloPaloFondo.setFont(Font.font("Arial", FontWeight.BOLD, 50));
                simboloPaloFondo.setTextFill(Color.GRAY.deriveColor(0, 1, 1, 0.3));
                simboloPaloFondo.setAlignment(Pos.CENTER);
                simboloPaloFondo.setMouseTransparent(true);
                getChildren().add(simboloPaloFondo);
            } else {
                // Error si el palo no se pudo determinar (no debería pasar)
                System.err.println("Error: No se pudo determinar el palo para la fundación índice " + indice);
            }
        }
        // --------------------------------------------------------

        setAlignment(Pos.CENTER);
        setPrefSize(CartaVista.CARTA_ANCHO, CartaVista.CARTA_ALTO);
    }

    // Este método determina qué palo corresponde a cada índice de fundación.
    private Palo getPaloFundacion(int index) {
        // Asigna un palo basado en el índice (0=PICAS, 1=CORAZONES, etc.)
        if (index >= 0 && index < Palo.values().length) {
            return Palo.values()[index];
        }
        System.err.println("Índice de fundación inválido: " + index);
        return Palo.PICAS; // O el primer palo disponible
    }


    private Rectangle crearBorde(Color color, double grosor, boolean discontinuo) {
        Rectangle borde = new Rectangle(CartaVista.CARTA_ANCHO + 4, CartaVista.CARTA_ALTO + 4);
        borde.setFill(Color.TRANSPARENT);
        borde.setStroke(color);
        borde.setStrokeWidth(grosor);
        if (discontinuo) {
            borde.getStrokeDashArray().addAll(10.0, 5.0);
        }
        borde.setArcWidth(12);
        borde.setArcHeight(12);
        borde.setVisible(false);
        borde.setMouseTransparent(true);
        return borde;
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

    // --- Métodos de Selección/Visualización ---
    public void seleccionarDestino(boolean s) { bordeDestino.setVisible(s); }
    public void visualizarOrigen(boolean s) { bordeHistorialOrigen.setVisible(s); }
    public void visualizarDestino(boolean s) { bordeHistorialDestino.setVisible(s); }

    public TipoLugar getTipo() { return tipo; }
    public int getIndice() { return indice; }
}