package vista;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import modelo.Carta;
import modelo.Rango;
import modelo.Palo;

// Make sure necessary imports are present
import java.io.InputStream;
import java.io.FileNotFoundException;


public class CartaVista extends StackPane {
    private Carta carta;
    private ImageView imageView;
    private Rectangle bordeSeleccion; // Dorado Origen Pista/Selección
    private Rectangle bordeDestino;   // Verde Destino Pista
    private Rectangle bordeHistorialOrigen; // Azul Origen Historial
    private Rectangle bordeHistorialDestino;// Púrpura Destino Historial

    public static final double CARTA_ANCHO = 70;
    public static final double CARTA_ALTO = 100;

    private static final String BASE_PATH = "/com/example/eightoff/individuals/";

    public CartaVista(Carta carta) {
        this.carta = carta;

        // 1. Construir la ruta completa de la imagen
        String rutaImagen = construirRutaImagen(carta);
        System.out.println("Intentando cargar: " + rutaImagen); // <-- DEBUG PRINT

        // 2. Cargar la imagen y crear el ImageView
        InputStream stream = null;
        try {
            // Intenta cargar desde la raíz del classpath
            stream = getClass().getResourceAsStream(rutaImagen);

            if (stream == null) {
                System.out.println("Intento 1 falló, intentando sin '/': " + rutaImagen.substring(1)); // <-- DEBUG PRINT
                stream = getClass().getResourceAsStream(rutaImagen.substring(1));
            }

            if (stream != null) {
                System.out.println("Stream encontrado para: " + rutaImagen); // <-- DEBUG PRINT
                Image imagenCarta = new Image(stream);
                if (imagenCarta.isError()) { // Check for image loading errors
                    System.err.println("Error DECODIFICANDO imagen: " + rutaImagen);
                    throw new RuntimeException("Error decodificando imagen: " + imagenCarta.getException().getMessage());
                }
                imageView = new ImageView(imagenCarta);
                imageView.setFitWidth(CARTA_ANCHO);
                imageView.setFitHeight(CARTA_ALTO);
                stream.close(); // Buena práctica cerrar el stream
            } else {
                System.err.println("STREAM ES NULL. Recurso NO ENCONTRADO para: " + rutaImagen); // <-- DEBUG PRINT
                throw new FileNotFoundException("Recurso no encontrado: " + rutaImagen + " (ni con / ni sin /)");
            }

        } catch (Exception e) {
            System.err.println("EXCEPCION al cargar/crear imagen para carta: " + carta.toString() + " en " + rutaImagen);
            e.printStackTrace(); // Imprime la traza completa
            // Placeholder MUY obvio si algo falla
            Rectangle placeholder = new Rectangle(CARTA_ANCHO, CARTA_ALTO, Color.MAGENTA); // Magenta brillante
            placeholder.setStroke(Color.YELLOW);
            placeholder.setStrokeWidth(3);
            getChildren().add(placeholder);
            imageView = null; // Asegura que imageView sea null si falló
        }

        // --- Bordes ---
        bordeSeleccion = crearBorde(Color.GOLD, 3, false);
        bordeDestino = crearBorde(Color.LIMEGREEN, 3, true);
        bordeHistorialOrigen = crearBorde(Color.DEEPSKYBLUE, 2, false);
        bordeHistorialDestino = crearBorde(Color.MEDIUMPURPLE, 2, true);

        // Añadir imagen (si existe) y bordes
        if (imageView != null) {
            getChildren().add(imageView);
        }
        getChildren().addAll(bordeSeleccion, bordeDestino, bordeHistorialOrigen, bordeHistorialDestino);

        setPrefSize(CARTA_ANCHO, CARTA_ALTO);
    }

    private Rectangle crearBorde(Color color, double grosor, boolean discontinuo) {
        Rectangle borde = new Rectangle(CARTA_ANCHO + 4, CARTA_ALTO + 4);
        borde.setFill(Color.TRANSPARENT);
        borde.setStroke(color);
        borde.setStrokeWidth(grosor);
        if (discontinuo) {
            borde.getStrokeDashArray().addAll(10.0, 5.0);
        }
        borde.setArcWidth(12);
        borde.setArcHeight(12);
        borde.setVisible(false);
        borde.setMouseTransparent(true);
        return borde;
    }

    private String construirRutaImagen(Carta carta) {
        StringBuilder sb = new StringBuilder(BASE_PATH);
        String nombrePaloCarpeta;
        switch (carta.getPalo()) {
            case PICAS: nombrePaloCarpeta = "spade"; break;
            case CORAZONES: nombrePaloCarpeta = "heart"; break;
            case DIAMANTES: nombrePaloCarpeta = "diamond"; break;
            // case TREBOLES:
            default: nombrePaloCarpeta = "club";
        }
        sb.append(nombrePaloCarpeta).append("/");

        String nombreRangoArchivo;
        if (carta.getRango() == Rango.JOTA) nombreRangoArchivo = "11";
        else if (carta.getRango() == Rango.REINA) nombreRangoArchivo = "12";
        else if (carta.getRango() == Rango.REY) nombreRangoArchivo = "13";
        else if (carta.getRango() == Rango.AS) nombreRangoArchivo = "1";
        else nombreRangoArchivo = String.valueOf(carta.getRango().getValor());

        sb.append(nombreRangoArchivo).append("_").append(nombrePaloCarpeta).append(".png");
        return sb.toString();
    }

    // --- Métodos de Selección/Visualización ---
    public void seleccionar(boolean s) { bordeSeleccion.setVisible(s); }
    public void seleccionarDestino(boolean s) { bordeDestino.setVisible(s); }
    public void visualizarOrigen(boolean s) { bordeHistorialOrigen.setVisible(s); }
    public void visualizarDestino(boolean s) { bordeHistorialDestino.setVisible(s); }

    public Carta getCarta() { return carta; }
}