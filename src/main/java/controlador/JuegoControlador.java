package controlador;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.input.MouseEvent; // Ya no se necesita D&D aquí
import modelo.Carta;
import modelo.JuegoEightOff;
import modelo.Movimiento;
import modelo.TipoLugar;
import vista.*;

public class JuegoControlador {

    private final JuegoEightOff modelo;
    private final JuegoVista vista;
    private final DragDropHandler dndHandler; // Se usa la clase separada

    public JuegoControlador(JuegoEightOff modelo, JuegoVista vista) {
        this.modelo = modelo;
        this.vista = vista;

        vincularBotones();

        // Define la acción de callback para el D&D
        Runnable onDropExitoso = () -> {
            actualizarVistaCompleta();
            verificarEstadoJuego();
        };

        // Crea e inicia el manejador de D&D
        this.dndHandler = new DragDropHandler(modelo, vista, onDropExitoso);
        this.dndHandler.vincularEventos();

        actualizarVistaCompleta();
    }

    private void vincularBotones() {
        vista.getControlesVista().getBtnNuevoJuego().setOnAction(e -> nuevoJuego());
        vista.getControlesVista().getBtnDeshacer().setOnAction(e -> deshacer());
        vista.getControlesVista().getBtnPista().setOnAction(e -> mostrarPista());
    }

    // --- Acciones de Botones ---

    private void nuevoJuego() {
        modelo.iniciarJuego();
        actualizarVistaCompleta();
    }

    private void deshacer() {
        if (modelo.deshacerMovimiento()) {
            actualizarVistaCompleta();
        }
    }

    private void mostrarPista() {
        Movimiento pista = modelo.buscarPista();
        if (pista == null) {
            verificarEstadoJuego();
            return;
        }

        CartaVista cartaPista = buscarCartaVista(pista.getCartaMovida());
        if (cartaPista != null) {
            cartaPista.seleccionar(true);
            new Thread(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException e) {}
                Platform.runLater(() -> cartaPista.seleccionar(false));
            }).start();
        }
    }

    // --- Lógica de Actualización y Estado ---

    private void actualizarVistaCompleta() {
        for (int i = 0; i < modelo.getCeldasReserva().length; i++) {
            vista.getCeldasReservaVista()[i].setCarta(modelo.getCeldasReserva()[i]);
        }
        for (int i = 0; i < modelo.getFundaciones().length; i++) {
            vista.getFundacionesVista()[i].setCarta(modelo.getFundaciones()[i].peek());
        }
        for (int i = 0; i < modelo.getTableau().length; i++) {
            vista.getTableauVista()[i].actualizar(modelo.getTableau()[i]);
        }
    }

    private void verificarEstadoJuego() {
        Platform.runLater(() -> {
            if (modelo.verificarVictoria()) {
                mostrarAlerta("¡Felicidades!", "¡Has ganado el juego!");
                nuevoJuego();
            } else if (modelo.verificarBloqueo()) {
                mostrarAlerta("Juego Terminado", "No hay más movimientos posibles. ¡Juego bloqueado!");
            }
        });
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private CartaVista buscarCartaVista(Carta cartaModelo) {
        if (cartaModelo == null) return null;

        for(CeldaVista cv : vista.getCeldasReservaVista()) {
            CartaVista c = cv.getCartaVista();
            if (c != null && c.getCarta().equals(cartaModelo)) return c;
        }

        for(ColumnaTableauVista col : vista.getTableauVista()) {
            CartaVista cv = col.getCartaVistaSuperior();
            if (cv != null && cv.getCarta().equals(cartaModelo)) {
                return cv;
            }
        }
        return null;
    }
}