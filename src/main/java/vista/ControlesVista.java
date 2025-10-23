package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public class ControlesVista extends HBox {
    private Button btnNuevoJuego;
    private Button btnDeshacer;
    private Button btnPista;

    public ControlesVista() {
        btnNuevoJuego = new Button("Nuevo Juego");
        btnDeshacer = new Button("Deshacer");
        btnPista = new Button("Pista");

        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);

        getChildren().addAll(btnNuevoJuego, btnDeshacer, btnPista);
    }

    public Button getBtnNuevoJuego() { return btnNuevoJuego; }
    public Button getBtnDeshacer() { return btnDeshacer; }
    public Button getBtnPista() { return btnPista; }
}