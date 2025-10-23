package vista;

import javafx.geometry.Pos;
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
import modelo.Carta;
import modelo.TipoLugar;
import modelo.estructuras.ListaSimple;

// Imports para la lista
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

public class ColumnaTableauVista extends StackPane {
    private int indice;

    private static final double SOLAPAMIENTO = 35; // Más espacio

    // Constructor simple (antes de Supermove)
    public ColumnaTableauVista(int indice) {
        super();
        this.indice = indice;
        setAlignment(Pos.TOP_LEFT);

        Color bgColor = Color.BLACK.deriveColor(1, 1, 1, 0.1);
        setBackground(new Background(new BackgroundFill(bgColor, new CornerRadii(10), Insets.EMPTY)));
        Color borderColor = Color.DARKGREEN;
        setBorder(new Border(new BorderStroke(borderColor, BorderStrokeStyle.SOLID,
                new CornerRadii(10), new BorderWidths(2))));

        setPrefWidth(CartaVista.CARTA_ANCHO);
    }

    public void actualizar(ListaSimple<Carta> columna) {
        getChildren().clear();
        double nuevaAltura;

        if (columna == null || columna.estaVacia()) {
            nuevaAltura = CartaVista.CARTA_ALTO;
        } else {
            List<Carta> cartas = new ArrayList<>(columna.getComoListaJava());
            // Si tu lista es [TOPE,...,FONDO], revierte
            // Collections.reverse(cartas);

            for (int i = 0; i < cartas.size(); i++) {
                CartaVista cv = new CartaVista(cartas.get(i));

                // ¡IMPORTANTE! Para D&D de una sola carta,
                // la carta superior debe ser transparente
                if (i == cartas.size() - 1) {
                    cv.setMouseTransparent(false); // La carta superior SÍ recibe clics
                } else {
                    cv.setMouseTransparent(true); // Las de abajo NO
                }

                cv.setManaged(false);
                cv.relocate(0, i * SOLAPAMIENTO);
                getChildren().add(cv);
            }

            nuevaAltura = CartaVista.CARTA_ALTO + (cartas.size() - 1) * SOLAPAMIENTO;
        }

        setPrefHeight(nuevaAltura);
    }

    // Este método SÍ era necesario para el D&D de una sola carta
    public CartaVista getCartaVistaSuperior() {
        return getChildren().stream()
                .filter(n -> n instanceof CartaVista)
                .map(n -> (CartaVista) n)
                .reduce((primero, segundo) -> segundo)
                .orElse(null);
    }

    public TipoLugar getTipo() { return TipoLugar.TABLEAU; }
    public int getIndice() { return indice; }
}