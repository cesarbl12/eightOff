package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import static javafx.scene.control.ContentDisplay.TOP;

public class ControlesVista extends VBox {
    // Botones de juego
    private Button btnNuevoJuego;
    private Button btnDeshacer;
    private Button btnPista;
    private Button btnVerHistorial;

    // Controles de historial
    private Button btnIrAlInicio;
    private Button btnRetrocederHistorial;
    private Button btnAvanzarHistorial;
    private Button btnIrAlFinal;
    private Button btnAplicarHistorial;
    private ListView<String> visorHistorial;

    public ControlesVista() {

        HBox filaJuego = new HBox(10);
        filaJuego.setAlignment(Pos.TOP_CENTER); // Mantiene los botones centrados horizontalmente
        btnNuevoJuego = new Button("Nuevo Juego");
        btnDeshacer = new Button("Deshacer");
        btnPista = new Button("Pista");
        btnVerHistorial = new Button("Ver Historial");

        filaJuego.getChildren().addAll(btnNuevoJuego, btnDeshacer, btnPista, btnVerHistorial);


        HBox filaHistorial = new HBox(5);
        filaHistorial.setAlignment(Pos.TOP_CENTER);
        btnIrAlInicio = new Button("<<");
        btnRetrocederHistorial = new Button("<");
        btnAvanzarHistorial = new Button(">");
        btnIrAlFinal = new Button(">>");
        btnAplicarHistorial = new Button("Aplicar Estado");
        btnAplicarHistorial.setDisable(true);
        filaHistorial.getChildren().addAll(
                btnIrAlInicio, btnRetrocederHistorial, btnAvanzarHistorial, btnIrAlFinal, btnAplicarHistorial
        );

        visorHistorial = new ListView<>();
        visorHistorial.setPrefHeight(100);

        btnVerHistorial.setOnAction(e -> {
            VBox historialLayout = new VBox(10);
            historialLayout.setPadding(new Insets(10));
            historialLayout.setAlignment(Pos.CENTER);

            historialLayout.getChildren().addAll(filaHistorial, visorHistorial);

            Stage historialStage = new Stage();
            historialStage.setTitle("Historial de Movimientos");
            historialStage.setScene(new Scene(historialLayout));
            historialStage.show();
        });

        getChildren().addAll(filaJuego);
    }

    // Getters para Controlador
    public Button getBtnNuevoJuego() { return btnNuevoJuego; }
    public Button getBtnDeshacer() { return btnDeshacer; }
    public Button getBtnPista() { return btnPista; }
    public Button getBtnVerHistorial() { return btnVerHistorial; }

    // Getters para los controles de historial
    public Button getBtnIrAlInicio() { return btnIrAlInicio; }
    public Button getBtnRetrocederHistorial() { return btnRetrocederHistorial; }
    public Button getBtnAvanzarHistorial() { return btnAvanzarHistorial; }
    public Button getBtnIrAlFinal() { return btnIrAlFinal; }
    public Button getBtnAplicarHistorial() { return btnAplicarHistorial; }
    public ListView<String> getVisorHistorial() { return visorHistorial; }
}