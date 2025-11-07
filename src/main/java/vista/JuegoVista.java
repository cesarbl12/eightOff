package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import modelo.JuegoEightOff;
import modelo.TipoLugar;

public class JuegoVista extends BorderPane {

    private CeldaVista[] celdasReservaVista;
    private CeldaVista[] fundacionesVista;
    private ColumnaTableauVista[] tableauVista;
    private ControlesVista controlesVista;

    public JuegoVista() {
        setStyle("-fx-background-color: #006400;");
        setPadding(new Insets(10));

        HBox zonaSuperior = new HBox(15);
        zonaSuperior.setPadding(new Insets(10));
        zonaSuperior.setAlignment(Pos.CENTER_LEFT);

        HBox celdasBox = new HBox(10);
        celdasReservaVista = new CeldaVista[JuegoEightOff.NUM_CELDAS];
        for (int i = 0; i < JuegoEightOff.NUM_CELDAS; i++) {
            celdasReservaVista[i] = new CeldaVista(TipoLugar.CELDA_RESERVA, i);
            celdasBox.getChildren().add(celdasReservaVista[i]);
        }

        Pane espaciador = new Pane();
        espaciador.setPrefWidth(50);

        HBox fundacionesBox = new HBox(10);
        fundacionesVista = new CeldaVista[JuegoEightOff.NUM_FUNDACIONES];
        for (int i = 0; i < JuegoEightOff.NUM_FUNDACIONES; i++) {
            fundacionesVista[i] = new CeldaVista(TipoLugar.FUNDACION, i);
            fundacionesVista[i].setStyle("-fx-border-color: #FFFFFF; -fx-border-width: 2; -fx-border-style: dashed;");
            fundacionesBox.getChildren().add(fundacionesVista[i]);
        }

        zonaSuperior.getChildren().addAll(celdasBox, espaciador, fundacionesBox);

        // Controles (botones)
        controlesVista = new ControlesVista();

        VBox topContainer = new VBox(8);
        topContainer.setPadding(new Insets(0, 10, 10, 10));
        topContainer.getChildren().addAll(controlesVista, zonaSuperior);

        setTop(topContainer);
        // setBottom(controlesVista);

        // --- Tableau al centro ---
        Region separador = new Region();
        separador.setMinHeight(50); // espacio entre la parte superior y el tableau
        topContainer.getChildren().add(separador);

        HBox tableauBox = new HBox(15);
        tableauBox.setAlignment(Pos.CENTER);
        tableauBox.setPadding(new Insets(20, 10, 10, 10)); // antes 100; ya no necesitas tanto margen
        tableauVista = new ColumnaTableauVista[JuegoEightOff.NUM_TABLEAUS];
        setCenter(tableauBox);
    }

    public CeldaVista[] getCeldasReservaVista() { return celdasReservaVista; }
    public CeldaVista[] getFundacionesVista() { return fundacionesVista; }
    public ColumnaTableauVista[] getTableauVista() { return tableauVista; }
    public ControlesVista getControlesVista() { return controlesVista; }
}
