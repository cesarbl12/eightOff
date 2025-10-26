package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView; // Importar
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox; // Importar

public class ControlesVista extends VBox { // Cambiado a VBox
    // Botones de juego
    private Button btnNuevoJuego;
    private Button btnDeshacer;
    private Button btnPista;

    // Controles de historial
    private Button btnIrAlInicio;
    private Button btnRetrocederHistorial;
    private Button btnAvanzarHistorial;
    private Button btnIrAlFinal;
    private Button btnAplicarHistorial;
    private ListView<String> visorHistorial; // Para mostrar los movimientos

    public ControlesVista() {
        setSpacing(10);
        setPadding(new Insets(10));
        setAlignment(Pos.CENTER);

        // --- Fila de Botones de Juego ---
        HBox filaJuego = new HBox(10);
        filaJuego.setAlignment(Pos.CENTER);
        btnNuevoJuego = new Button("Nuevo Juego");
        btnDeshacer = new Button("Deshacer"); // Este botón ahora puede usar el historial
        btnPista = new Button("Pista");
        filaJuego.getChildren().addAll(btnNuevoJuego, btnDeshacer, btnPista);

        // --- Fila de Controles de Historial ---
        HBox filaHistorial = new HBox(5); // Menos espacio entre botones de historial
        filaHistorial.setAlignment(Pos.CENTER);
        btnIrAlInicio = new Button("<<");
        btnRetrocederHistorial = new Button("<");
        btnAvanzarHistorial = new Button(">");
        btnIrAlFinal = new Button(">>");
        btnAplicarHistorial = new Button("Aplicar Estado");
        // Deshabilitar Aplicar por defecto (solo se activa en modo historial)
        btnAplicarHistorial.setDisable(true);
        filaHistorial.getChildren().addAll(
                btnIrAlInicio, btnRetrocederHistorial, btnAvanzarHistorial, btnIrAlFinal, btnAplicarHistorial
        );

        // --- Visor de Historial ---
        visorHistorial = new ListView<>();
        visorHistorial.setPrefHeight(100); // Altura limitada para el visor

        // Añadir filas y visor al VBox principal
        getChildren().addAll(filaJuego, filaHistorial, visorHistorial);
    }

    // Getters para Controlador
    public Button getBtnNuevoJuego() { return btnNuevoJuego; }
    public Button getBtnDeshacer() { return btnDeshacer; }
    public Button getBtnPista() { return btnPista; }

    public Button getBtnIrAlInicio() { return btnIrAlInicio; }
    public Button getBtnRetrocederHistorial() { return btnRetrocederHistorial; }
    public Button getBtnAvanzarHistorial() { return btnAvanzarHistorial; }
    public Button getBtnIrAlFinal() { return btnIrAlFinal; }
    public Button getBtnAplicarHistorial() { return btnAplicarHistorial; }
    public ListView<String> getVisorHistorial() { return visorHistorial; }
}