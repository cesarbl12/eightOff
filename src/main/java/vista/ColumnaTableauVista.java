package vista;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.geometry.Insets;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import controlador.JuegoControlador;
import modelo.Carta;
import modelo.TipoLugar;
import modelo.estructuras.ListaSimple;

import java.util.List;
import java.util.ArrayList;


public class ColumnaTableauVista extends StackPane {
    private int indice;
    private JuegoControlador controlador;
    private Rectangle bordeDestino;
    private Rectangle bordeHistorialOrigen;
    private Rectangle bordeHistorialDestino;
    private TipoLugar tipo;

    private static final double SOLAPAMIENTO = 35;

    public ColumnaTableauVista(int indice, JuegoControlador controlador) {
        super();
        this.indice = indice;
        this.controlador = controlador;
        this.tipo = TipoLugar.TABLEAU;

        setAlignment(Pos.TOP_CENTER);
        setBackground(new Background(new BackgroundFill(Color.BLACK.deriveColor(1, 1, 1, 0.1), new CornerRadii(10), Insets.EMPTY)));
        setBorder(new Border(new BorderStroke(Color.DARKGREEN, BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
        setPrefWidth(CartaVista.CARTA_ANCHO + 4);

        bordeDestino = crearBorde(Color.LIMEGREEN, 3, true);
        bordeHistorialOrigen = crearBorde(Color.DEEPSKYBLUE, 2, false);
        bordeHistorialDestino = crearBorde(Color.MEDIUMPURPLE, 2, true);

        getChildren().addAll(bordeDestino, bordeHistorialOrigen, bordeHistorialDestino);
        StackPane.setAlignment(bordeDestino, Pos.TOP_CENTER);
        StackPane.setAlignment(bordeHistorialOrigen, Pos.TOP_CENTER);
        StackPane.setAlignment(bordeHistorialDestino, Pos.TOP_CENTER);

        controlador.vincularDestinoDrop(this, TipoLugar.TABLEAU, indice);
    }

    private Rectangle crearBorde(Color color, double grosor, boolean discontinuo) {
        Rectangle borde = new Rectangle();
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

    public void actualizar(ListaSimple<Carta> columna) {
        getChildren().removeIf(node -> node instanceof CartaVista);

        double nuevaAltura;

        if (columna == null || columna.estaVacia()) {
            nuevaAltura = CartaVista.CARTA_ALTO;
        } else {
            List<Carta> cartas = new ArrayList<>(columna.getComoListaJava());
            double cardOffsetX = (getPrefWidth() - CartaVista.CARTA_ANCHO) / 2;

            for (int i = 0; i < cartas.size(); i++) {
                CartaVista cv = new CartaVista(cartas.get(i));
                cv.setManaged(false);
                cv.relocate(cardOffsetX, i * SOLAPAMIENTO);
                getChildren().add(cv);
                controlador.vincularEventosCarta(cv, this.indice, i, cartas.size());
            }
            nuevaAltura = CartaVista.CARTA_ALTO + (cartas.size() - 1) * SOLAPAMIENTO;
        }
        setPrefHeight(nuevaAltura);

        ajustarBorde(bordeDestino, nuevaAltura);
        ajustarBorde(bordeHistorialOrigen, nuevaAltura);
        ajustarBorde(bordeHistorialDestino, nuevaAltura);
    }

    private void ajustarBorde(Rectangle borde, double alturaContenido) {
        borde.setWidth(CartaVista.CARTA_ANCHO + 4);
        borde.setHeight(alturaContenido + 4);
    }

    public void seleccionarDestino(boolean s) { bordeDestino.setVisible(s); }
    public void visualizarOrigen(boolean s) { bordeHistorialOrigen.setVisible(s); }
    public void visualizarDestino(boolean s) { bordeHistorialDestino.setVisible(s); }

    public CartaVista getCartaVistaSuperior() {
        return getChildren().stream()
                .filter(n -> n instanceof CartaVista)
                .map(n -> (CartaVista) n)
                .reduce((primero, segundo) -> segundo)
                .orElse(null);
    }

    public int getIndice() { return indice; }

    public TipoLugar getTipo() {
        return this.tipo;
    }
}