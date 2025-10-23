package principal;

import controlador.JuegoControlador;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import modelo.JuegoEightOff;
import vista.JuegoVista;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 1. Crear el Modelo
        JuegoEightOff modelo = new JuegoEightOff();

        // 2. Crear la Vista
        JuegoVista vista = new JuegoVista();

        // 3. Crear el Controlador (y vincularlos)
        new JuegoControlador(modelo, vista);

        // 4. Configurar la Escena y el Escenario
        Scene scene = new Scene(vista, 1000, 700); // Un poco más de alto

        primaryStage.setTitle("Eight Off Solitaire");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}