package controlador;

import javafx.scene.Node;
import javafx.scene.input.*;
import modelo.JuegoEightOff;
import modelo.TipoLugar;
import vista.CartaVista;
import vista.CeldaVista;
import vista.ColumnaTableauVista;
import vista.JuegoVista;

public class DragDropHandler {

    private final JuegoEightOff modelo;
    private final JuegoVista vista;
    private final Runnable onMovimientoExitoso;

    private static final DataFormat FORMATO_CARTA = new DataFormat("modelo.Carta");

    public DragDropHandler(JuegoEightOff modelo, JuegoVista vista, Runnable onMovimientoExitoso) {
        this.modelo = modelo;
        this.vista = vista;
        this.onMovimientoExitoso = onMovimientoExitoso;
    }

    public void vincularEventos() {
        for (CeldaVista cv : vista.getCeldasReservaVista()) {
            vincularFuenteDrag(cv, cv.getTipo(), cv.getIndice());
        }
        for (ColumnaTableauVista tv : vista.getTableauVista()) {
            vincularFuenteDrag(tv, tv.getTipo(), tv.getIndice());
        }

        for (CeldaVista cv : vista.getCeldasReservaVista()) {
            vincularDestinoDrop(cv, cv.getTipo(), cv.getIndice());
        }
        for (CeldaVista fv : vista.getFundacionesVista()) {
            vincularDestinoDrop(fv, fv.getTipo(), fv.getIndice());
        }
        for (ColumnaTableauVista tv : vista.getTableauVista()) {
            vincularDestinoDrop(tv, tv.getTipo(), tv.getIndice());
        }
    }

    private void vincularFuenteDrag(Node nodo, TipoLugar tipo, int indice) {
        nodo.setOnDragDetected((MouseEvent event) -> {
            CartaVista cartaVista = null;
            if (nodo instanceof CeldaVista) {
                cartaVista = ((CeldaVista) nodo).getCartaVista();
            } else if (nodo instanceof ColumnaTableauVista) {
                cartaVista = ((ColumnaTableauVista) nodo).getCartaVistaSuperior();
            }

            if (cartaVista != null) {
                Dragboard db = nodo.startDragAndDrop(TransferMode.MOVE);
                db.setDragView(cartaVista.snapshot(null, null));
                ClipboardContent content = new ClipboardContent();
                String origenData = tipo.name() + ":" + indice;
                content.put(FORMATO_CARTA, origenData);
                db.setContent(content);
                event.consume();
            }
        });
    }

    private void vincularDestinoDrop(Node nodo, TipoLugar destinoTipo, int destinoIndice) {

        nodo.setOnDragOver((DragEvent event) -> {
            if (event.getGestureSource() != nodo && event.getDragboard().hasContent(FORMATO_CARTA)) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        nodo.setOnDragDropped((DragEvent event) -> {
            Dragboard db = event.getDragboard();
            boolean exito = false;

            if (db.hasContent(FORMATO_CARTA)) {
                String origenData = (String) db.getContent(FORMATO_CARTA);
                String[] parts = origenData.split(":");
                TipoLugar origenTipo = TipoLugar.valueOf(parts[0]);
                int origenIndice = Integer.parseInt(parts[1]);

                exito = modelo.intentarMover(origenTipo, origenIndice, destinoTipo, destinoIndice);

                if (exito) {
                    onMovimientoExitoso.run();
                }
            }

            event.setDropCompleted(exito);
            event.consume();
        });
    }
}
