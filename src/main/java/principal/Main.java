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
        JuegoEightOff modelo = new JuegoEightOff();
        JuegoVista vista = new JuegoVista();
        new JuegoControlador(modelo, vista);

        Scene scene = new Scene(vista, 1200, 700);
        primaryStage.setTitle("Eight Off Solitaire");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
