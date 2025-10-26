package controlador;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.input.*;
import javafx.scene.layout.HBox;
import modelo.JuegoEightOff;
import modelo.Movimiento;
import modelo.TipoLugar;
import modelo.estructuras.ListaSimple;
import modelo.Carta;
import vista.*;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class JuegoControlador {

    private final JuegoEightOff modelo;
    private final JuegoVista vista;

    private static final DataFormat FORMATO_CARTA = new DataFormat("modelo.CartaSecuencia");

    private Node pistaOrigen = null;
    private Node pistaDestino = null;

    private boolean isInHistoryMode = false;
    private Node historialVisualOrigen = null;
    private Node historialVisualDestino = null;
    private ObservableList<String> historialItems = FXCollections.observableArrayList();

    public JuegoControlador(JuegoEightOff modelo, JuegoVista vista) {
        this.modelo = modelo;
        this.vista = vista;

        // Crear Columnas
        for (int i = 0; i < JuegoEightOff.NUM_TABLEAUS; i++) {
            ColumnaTableauVista colVista = new ColumnaTableauVista(i, this);
            vista.getTableauVista()[i] = colVista;
            Node centerNode = vista.getCenter();
            if (centerNode instanceof HBox) {
                ((HBox) centerNode).getChildren().add(colVista);
            } else {
                System.err.println("Error: El centro de JuegoVista no es un HBox.");
            }
        }

        vincularBotones();
        vincularDestinosSimples();
        vincularVisorHistorial();

        actualizarVistaCompleta();
        actualizarVisorHistorial();
    }

    private void vincularBotones() {
        vista.getControlesVista().getBtnNuevoJuego().setOnAction(e -> {
            limpiarPistaYVisualizacion();
            salirModoHistorial();
            nuevoJuego();
        });
        vista.getControlesVista().getBtnDeshacer().setOnAction(e -> {
            limpiarPistaYVisualizacion();
            if (!isInHistoryMode) {
                if (modelo.undoMovimiento()) {
                    actualizarVistaCompleta();
                    actualizarVisorHistorial();
                }
            }
        });
        vista.getControlesVista().getBtnPista().setOnAction(e -> {
            if (!isInHistoryMode) mostrarPista();
        });

        vista.getControlesVista().getBtnIrAlInicio().setOnAction(e -> navegarHistorialInicio());
        vista.getControlesVista().getBtnRetrocederHistorial().setOnAction(e -> navegarHistorialAtras());
        vista.getControlesVista().getBtnAvanzarHistorial().setOnAction(e -> navegarHistorialAdelante());
        vista.getControlesVista().getBtnIrAlFinal().setOnAction(e -> navegarHistorialFinal());
        vista.getControlesVista().getBtnAplicarHistorial().setOnAction(e -> aplicarEstadoHistorial());
    }

    private void vincularDestinosSimples() {
        for (CeldaVista cv : vista.getCeldasReservaVista()) {
            vincularFuenteDrag(cv, cv.getTipo(), cv.getIndice(), 1);
            vincularDestinoDrop(cv, cv.getTipo(), cv.getIndice());
        }
        for (CeldaVista fv : vista.getFundacionesVista()) {
            vincularDestinoDrop(fv, fv.getTipo(), fv.getIndice());
        }
    }

    public void vincularEventosCarta(CartaVista cv, int colIndice, int cardIndiceEnPila, int totalCartasPila) {
        int numCartasSecuencia = totalCartasPila - cardIndiceEnPila;
        vincularFuenteDrag(cv, TipoLugar.TABLEAU, colIndice, numCartasSecuencia);

        if (cardIndiceEnPila == totalCartasPila - 1) { // Solo la carta TOPE es destino
            vincularDestinoDrop(cv, TipoLugar.TABLEAU, colIndice);
        }
    }

    // --- LÓGICA D&D ---
    private void vincularFuenteDrag(Node nodo, TipoLugar tipo, int indice, int N) {
        nodo.setOnDragDetected((MouseEvent event) -> {
            if (isInHistoryMode) { event.consume(); return; }
            limpiarPistaYVisualizacion();
            if (tipo == TipoLugar.TABLEAU && N > 1) {
                if (!esSecuenciaValidaEnModelo(tipo, indice, N)) {
                    event.consume(); return;
                }
            }
            Dragboard db = nodo.startDragAndDrop(TransferMode.MOVE);
            db.setDragView(nodo.snapshot(null, null));
            ClipboardContent content = new ClipboardContent();
            String origenData = tipo.name() + ":" + indice + ":" + N;
            content.put(FORMATO_CARTA, origenData);
            db.setContent(content);
            event.consume();
        });
    }

    private boolean esSecuenciaValidaEnModelo(TipoLugar tipo, int indice, int N) {
        if (tipo != TipoLugar.TABLEAU) return true;
        ListaSimple<Carta> columna = modelo.getTableau()[indice];
        if (columna == null || columna.getTamano() < N) return false; // Añadir chequeo null
        List<Carta> cartas = new ArrayList<>(columna.getComoListaJava());
        // Collections.reverse(cartas); // Asegúrate que esté comentado si tu lista es [FONDO...TOPE]
        int indiceBase = cartas.size() - N;
        for (int i = indiceBase; i < cartas.size() - 1; i++) {
            Carta actual = cartas.get(i);
            Carta siguiente = cartas.get(i+1);
            if (!siguiente.esAnteriorMismoPalo(actual)) return false;
        }
        return true;
    }

    public void vincularDestinoDrop(Node nodo, TipoLugar destinoTipo, int destinoIndice) {
        nodo.setOnDragOver((DragEvent event) -> {
            if (isInHistoryMode) { event.consume(); return; }
            if (event.getGestureSource() != nodo && event.getDragboard().hasContent(FORMATO_CARTA)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        nodo.setOnDragDropped((DragEvent event) -> {
            if (isInHistoryMode) { event.consume(); return; }
            Dragboard db = event.getDragboard();
            boolean exito = false;
            if (db.hasContent(FORMATO_CARTA)) {
                String origenData = (String) db.getContent(FORMATO_CARTA);
                String[] parts = origenData.split(":");
                TipoLugar origenTipo = TipoLugar.valueOf(parts[0]);
                int origenIndice = Integer.parseInt(parts[1]);
                int N = Integer.parseInt(parts[2]);

                limpiarPistaYVisualizacion();
                exito = modelo.intentarMoverSecuencia(origenTipo, origenIndice, destinoTipo, destinoIndice, N);

                if (exito) {
                    actualizarVistaCompleta();
                    verificarEstadoJuego();
                    actualizarVisorHistorial();
                }
            }
            event.setDropCompleted(exito);
            event.consume();
        });
    }

    // --- Funciones de Historial ---
    private void entrarModoHistorial() {
        if (!isInHistoryMode) {
            isInHistoryMode = true;
            vista.getControlesVista().getBtnAplicarHistorial().setDisable(false);
            vista.setOpacity(0.8);
        }
        limpiarPista();
    }

    private void salirModoHistorial() {
        if (isInHistoryMode) {
            isInHistoryMode = false;
            vista.getControlesVista().getBtnAplicarHistorial().setDisable(true);
            limpiarVisualizacionHistorial();
            vista.setOpacity(1.0);
            // Ya NO se fuerza ir al final aquí
            actualizarVistaCompleta(); // La vista ya refleja el estado del cursor
            actualizarVisorHistorial(); // Sincroniza botones
        }
    }

    private void actualizarVisorHistorial() {
        List<Movimiento> movimientos = modelo.getHistorialCompleto();
        List<String> descripciones = movimientos.stream()
                .map(this::formatearMovimiento)
                .collect(Collectors.toList());

        // Añadir "Inicio del Juego" si no hay movimientos
        if (descripciones.isEmpty() && modelo.estaEnInicio()) {
            historialItems.setAll("Inicio del Juego");
        } else {
            historialItems.setAll(descripciones);
        }

        ListView<String> visor = vista.getControlesVista().getVisorHistorial();
        // Preservar selección si es posible
        int currentSelection = visor.getSelectionModel().getSelectedIndex();
        visor.setItems(historialItems);

        int cursorPos = modelo.getPosicionCursorHistorial();
        // El cursor apunta al último movimiento HECHO. El índice de la lista es ese mismo valor.
        // Si cursorPos es -1 (inicio), no seleccionar nada.
        if (cursorPos >= 0 && cursorPos < historialItems.size()) {
            visor.getSelectionModel().select(cursorPos);
            visor.scrollTo(cursorPos);
        } else {
            visor.getSelectionModel().clearSelection();
            // Si estamos al inicio y hay elementos, seleccionar el primero visualmente
            if (modelo.estaEnInicio() && !historialItems.isEmpty() && historialItems.get(0).equals("Inicio del Juego")) {
                // No seleccionar nada formalmente, pero mostrar el inicio
            } else if (modelo.estaEnInicio()) {
                visor.getSelectionModel().clearSelection(); // Asegurar deselección
            }
        }


        // Habilitar/deshabilitar botones de navegación
        vista.getControlesVista().getBtnRetrocederHistorial().setDisable(modelo.estaEnInicio());
        vista.getControlesVista().getBtnIrAlInicio().setDisable(modelo.estaEnInicio());
        vista.getControlesVista().getBtnAvanzarHistorial().setDisable(modelo.estaEnFinal());
        vista.getControlesVista().getBtnIrAlFinal().setDisable(modelo.estaEnFinal());
    }

    private String formatearMovimiento(Movimiento mov) {
        if (mov == null) return "Inicio del Juego";
        String cartaStr = mov.getCartaMovida().toString();
        if (mov.getNumCartas() > 1) cartaStr += " (" + mov.getNumCartas() + ")";
        String origenStr = mov.getOrigenTipo().name().substring(0,3) + mov.getOrigenIndice();
        String destinoStr = mov.getDestinoTipo().name().substring(0,3) + mov.getDestinoIndice();
        return String.format("%s: %s -> %s", cartaStr, origenStr, destinoStr);
    }

    private void vincularVisorHistorial() {
        ListView<String> visor = vista.getControlesVista().getVisorHistorial();
        visor.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && visor.getSelectionModel().getSelectedIndex() != -1) {
                int selectedIndex = visor.getSelectionModel().getSelectedIndex();
                int currentIndex = modelo.getPosicionCursorHistorial();

                // Convertir índice de lista a posición de cursor modelo
                // Si la lista tiene "Inicio del Juego", el índice 0 corresponde a cursor -1
                // En general, selectedIndex == currentIndex

                if (selectedIndex != currentIndex) {
                    entrarModoHistorial();

                    while (selectedIndex < currentIndex) {
                        modelo.undoMovimiento();
                        currentIndex--;
                    }
                    while (selectedIndex > currentIndex) {
                        modelo.redoMovimiento();
                        currentIndex++;
                    }
                    actualizarVistaCompleta();
                    visualizarMovimientoSeleccionado();
                    // Re-actualizar botones por si llegamos al inicio/final
                    actualizarVisorHistorial(); // Sincroniza selección y botones
                }
            } else if (newVal == null) {
                // Si se deselecciona, limpiar visualización si estamos en modo historial
                if (isInHistoryMode) {
                    limpiarVisualizacionHistorial();
                }
            }
        });
    }

    private void limpiarVisualizacionHistorial() {
        if (historialVisualOrigen != null) {
            if (historialVisualOrigen instanceof CartaVista) ((CartaVista) historialVisualOrigen).visualizarOrigen(false);
            if (historialVisualOrigen instanceof CeldaVista) ((CeldaVista) historialVisualOrigen).visualizarOrigen(false);
        }
        if (historialVisualDestino != null) {
            if (historialVisualDestino instanceof CartaVista) ((CartaVista) historialVisualDestino).visualizarDestino(false);
            if (historialVisualDestino instanceof CeldaVista) ((CeldaVista) historialVisualDestino).visualizarDestino(false);
            if (historialVisualDestino instanceof ColumnaTableauVista) ((ColumnaTableauVista) historialVisualDestino).visualizarDestino(false);
        }
        historialVisualOrigen = null;
        historialVisualDestino = null;
    }

    private void visualizarMovimientoSeleccionado() {
        limpiarVisualizacionHistorial();
        Movimiento mov = modelo.getMovimientoEnCursor();
        if (mov == null) return; // Estado inicial

        historialVisualOrigen = buscarNodoOrigenVisual(mov.getOrigenTipo(), mov.getOrigenIndice(), mov.getCartaMovida());
        if (historialVisualOrigen != null) {
            if (historialVisualOrigen instanceof CartaVista) ((CartaVista) historialVisualOrigen).visualizarOrigen(true);
            if (historialVisualOrigen instanceof CeldaVista) ((CeldaVista) historialVisualOrigen).visualizarOrigen(true);
        }

        historialVisualDestino = buscarNodoDestino(mov.getDestinoTipo(), mov.getDestinoIndice());
        if (historialVisualDestino != null) {
            if (historialVisualDestino instanceof CartaVista) ((CartaVista) historialVisualDestino).visualizarDestino(true);
            if (historialVisualDestino instanceof CeldaVista) ((CeldaVista) historialVisualDestino).visualizarDestino(true);
            if (historialVisualDestino instanceof ColumnaTableauVista) ((ColumnaTableauVista) historialVisualDestino).visualizarDestino(true);
        }
    }

    private Node buscarNodoOrigenVisual(TipoLugar tipo, int indice, Carta cartaBaseModelo) {
        if (tipo == TipoLugar.CELDA_RESERVA) {
            return vista.getCeldasReservaVista()[indice];
        }
        if (tipo == TipoLugar.TABLEAU) {
            return buscarCartaVista(cartaBaseModelo); // Busca la carta base específica
        }
        return null;
    }

    private void navegarHistorialInicio() {
        entrarModoHistorial();
        modelo.irAlInicioHistorial();
        actualizarVistaCompleta();
        actualizarVisorHistorial();
        visualizarMovimientoSeleccionado(); // Limpiará porque getMovimientoEnCursor será null
    }
    private void navegarHistorialAtras() {
        entrarModoHistorial();
        if(modelo.undoMovimiento()) {
            actualizarVistaCompleta();
            actualizarVisorHistorial();
            visualizarMovimientoSeleccionado();
        }
    }
    private void navegarHistorialAdelante() {
        entrarModoHistorial();
        if(modelo.redoMovimiento()) {
            actualizarVistaCompleta();
            actualizarVisorHistorial();
            visualizarMovimientoSeleccionado();
        }
    }
    private void navegarHistorialFinal() {
        entrarModoHistorial();
        modelo.irAlFinalHistorial();
        actualizarVistaCompleta();
        actualizarVisorHistorial();
        visualizarMovimientoSeleccionado();
    }

    // --- ¡MÉTODO CORREGIDO! ---
    private void aplicarEstadoHistorial() {
        if (isInHistoryMode) {
            // 1. Decirle al modelo que trunque el historial futuro
            modelo.aplicarEstadoActualHistorial();

            // 2. Salir del modo historial (NO va al final)
            isInHistoryMode = false;
            vista.getControlesVista().getBtnAplicarHistorial().setDisable(true);
            limpiarVisualizacionHistorial();
            vista.setOpacity(1.0);

            // 3. Actualizar la vista y el visor para reflejar el estado truncado
            actualizarVistaCompleta(); // Redibuja el tablero en el estado actual
            actualizarVisorHistorial(); // Actualiza la lista y botones del historial
        }
    }


    // --- Métodos de Ayuda y Estado ---
    private void nuevoJuego() {
        modelo.iniciarJuego();
        actualizarVistaCompleta();
        actualizarVisorHistorial();
    }
    private void deshacer() { // Botón Deshacer normal
        if (!isInHistoryMode) {
            limpiarPistaYVisualizacion(); // Limpiar si había pista
            if (modelo.undoMovimiento()) {
                actualizarVistaCompleta();
                actualizarVisorHistorial();
            }
        }
    }

    private void limpiarPista() {
        if (pistaOrigen != null) {
            if (pistaOrigen instanceof CartaVista) ((CartaVista) pistaOrigen).seleccionar(false);
            if (pistaOrigen instanceof CeldaVista) {
                CartaVista cv = ((CeldaVista)pistaOrigen).getCartaVista();
                if (cv != null) cv.seleccionar(false);
            }
        }
        if (pistaDestino != null) {
            if (pistaDestino instanceof CartaVista) ((CartaVista) pistaDestino).seleccionarDestino(false);
            if (pistaDestino instanceof CeldaVista) ((CeldaVista) pistaDestino).seleccionarDestino(false);
            if (pistaDestino instanceof ColumnaTableauVista) ((ColumnaTableauVista) pistaDestino).seleccionarDestino(false);
        }
        pistaOrigen = null;
        pistaDestino = null;
    }

    private void limpiarPistaYVisualizacion() {
        limpiarPista();
        limpiarVisualizacionHistorial();
    }

    private Node buscarNodoDestino(TipoLugar tipo, int indice) {
        switch (tipo) {
            case CELDA_RESERVA: return vista.getCeldasReservaVista()[indice];
            case FUNDACION: return vista.getFundacionesVista()[indice];
            case TABLEAU:
                ColumnaTableauVista col = vista.getTableauVista()[indice];
                CartaVista topCard = (col != null) ? col.getCartaVistaSuperior() : null;
                return (topCard != null) ? topCard : col;
        }
        return null;
    }

    private CartaVista buscarCartaVista(Carta cartaModelo) {
        if (cartaModelo == null) return null;
        for(CeldaVista cv : vista.getCeldasReservaVista()) {
            CartaVista c = cv.getCartaVista();
            if (c != null && c.getCarta().equals(cartaModelo)) return c;
        }
        for(ColumnaTableauVista col : vista.getTableauVista()) {
            if (col == null) continue;
            for(Node n : col.getChildren()) {
                if (n instanceof CartaVista) {
                    CartaVista cv = (CartaVista)n;
                    if (cv.getCarta().equals(cartaModelo)) {
                        return cv;
                    }
                }
            }
        }
        return null;
    }

    private void mostrarPista() {
        limpiarPistaYVisualizacion();
        Movimiento pista = modelo.buscarPista();
        if (pista == null) {
            verificarEstadoJuego();
            return;
        }

        pistaOrigen = buscarNodoOrigenVisual(pista.getOrigenTipo(), pista.getOrigenIndice(), pista.getCartaMovida());
        if (pistaOrigen != null) {
            if (pistaOrigen instanceof CartaVista) ((CartaVista)pistaOrigen).seleccionar(true);
            if (pistaOrigen instanceof CeldaVista) {
                CartaVista cv = ((CeldaVista)pistaOrigen).getCartaVista();
                if(cv != null) cv.seleccionar(true);
            }
        }

        pistaDestino = buscarNodoDestino(pista.getDestinoTipo(), pista.getDestinoIndice());
        if (pistaDestino != null) {
            if (pistaDestino instanceof CartaVista) ((CartaVista) pistaDestino).seleccionarDestino(true);
            if (pistaDestino instanceof CeldaVista) ((CeldaVista) pistaDestino).seleccionarDestino(true);
            if (pistaDestino instanceof ColumnaTableauVista) ((ColumnaTableauVista) pistaDestino).seleccionarDestino(true);
        }
    }

    private void verificarEstadoJuego() {
        Platform.runLater(() -> {
            if (modelo.verificarVictoria()) {
                mostrarAlerta("¡Felicidades!", "¡Has ganado el juego!");
                salirModoHistorial(); // Salir del modo historial si ganó
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

    private void actualizarVistaCompleta() {
        // Celdas
        for (int i = 0; i < modelo.getCeldasReserva().length; i++) {
            if (vista.getCeldasReservaVista() != null && vista.getCeldasReservaVista()[i] != null)
                vista.getCeldasReservaVista()[i].setCarta(modelo.getCeldasReserva()[i]);
        }
        // Fundaciones
        for (int i = 0; i < modelo.getFundaciones().length; i++) {
            if (vista.getFundacionesVista() != null && vista.getFundacionesVista()[i] != null)
                vista.getFundacionesVista()[i].setCarta(modelo.getFundaciones()[i].peek());
        }
        // Tableau
        for (int i = 0; i < modelo.getTableau().length; i++) {
            if (vista.getTableauVista() != null && vista.getTableauVista()[i] != null) {
                vista.getTableauVista()[i].actualizar(modelo.getTableau()[i]);
            }
        }
    }
}