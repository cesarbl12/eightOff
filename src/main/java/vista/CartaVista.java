package vista;

import javafx.scene.image.Image; // Importar Image
import javafx.scene.image.ImageView; // Importar ImageView
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle; // Mantener para el borde de selección
import javafx.scene.paint.Color; // Mantener para el borde de selección
import modelo.Carta;
import modelo.Rango;
import modelo.Palo;

public class CartaVista extends StackPane {
    private Carta carta;
    private ImageView imageView; // Nuevo: Para mostrar la imagen de la carta
    private Rectangle bordeSeleccion; // Se mantiene para el efecto de selección

    public static final double CARTA_ANCHO = 70;
    public static final double CARTA_ALTO = 100;

    // --- RUTA BASE DE LAS IMÁGENES ---
    // Esta es la ruta dentro de los recursos del JAR o IDE
    // "com/example/eightoff/individuals" es la ruta relativa dentro de resources
    private static final String BASE_PATH = "/com/example/eightoff/individuals/";

    public CartaVista(Carta carta) {
        this.carta = carta;

        // 1. Construir la ruta completa de la imagen
        String rutaImagen = construirRutaImagen(carta);

        // 2. Cargar la imagen y crear el ImageView
        try {
            Image imagenCarta = new Image(getClass().getResourceAsStream(rutaImagen));
            imageView = new ImageView(imagenCarta);
            // Ajustar el tamaño del ImageView para que coincida con CARTA_ANCHO y CARTA_ALTO
            imageView.setFitWidth(CARTA_ANCHO);
            imageView.setFitHeight(CARTA_ALTO);
        } catch (Exception e) {
            // Manejo de errores si la imagen no se encuentra
            System.err.println("Error al cargar imagen para carta: " + carta.toString() + " en " + rutaImagen);
            System.err.println("Verifica que la ruta y el nombre del archivo sean correctos.");
            // Si la imagen falla, puedes mostrar un placeholder o una carta genérica
            Rectangle placeholder = new Rectangle(CARTA_ANCHO, CARTA_ALTO, Color.LIGHTGRAY);
            placeholder.setStroke(Color.RED);
            placeholder.setStrokeWidth(2);
            getChildren().add(placeholder);
            return; // Salir si la imagen no se carga
        }

        // --- Borde de selección (se mantiene) ---
        bordeSeleccion = new Rectangle(CARTA_ANCHO + 4, CARTA_ALTO + 4);
        bordeSeleccion.setFill(Color.TRANSPARENT);
        bordeSeleccion.setStroke(Color.GOLD);
        bordeSeleccion.setStrokeWidth(3);
        bordeSeleccion.setArcWidth(12);
        bordeSeleccion.setArcHeight(12);
        bordeSeleccion.setVisible(false);

        // Añadir el ImageView y el borde de selección al StackPane
        getChildren().addAll(imageView, bordeSeleccion);

        // El ImageView por defecto se centrará en el StackPane
        // El borde de selección también

        setPrefSize(CARTA_ANCHO, CARTA_ALTO);
    }

    // --- Método auxiliar para construir la ruta de la imagen ---
    private String construirRutaImagen(Carta carta) {
        StringBuilder sb = new StringBuilder(BASE_PATH);

        // Determinar la carpeta del palo
        String nombrePaloCarpeta;
        switch (carta.getPalo()) {
            case PICAS: nombrePaloCarpeta = "spade"; break;
            case CORAZONES: nombrePaloCarpeta = "heart"; break;
            case DIAMANTES: nombrePaloCarpeta = "diamond"; break;
            case TREBOLES: nombrePaloCarpeta = "club"; break;
            default: nombrePaloCarpeta = "unknown"; // Caso de seguridad
        }
        sb.append(nombrePaloCarpeta).append("/");

        // Determinar el nombre del archivo de la carta
        String nombreRangoArchivo;
        if (carta.getRango() == Rango.JOTA) {
            nombreRangoArchivo = "11";
        } else if (carta.getRango() == Rango.REINA) {
            nombreRangoArchivo = "12";
        } else if (carta.getRango() == Rango.REY) {
            nombreRangoArchivo = "13";
        } else if (carta.getRango() == Rango.AS) {
            nombreRangoArchivo = "1";
        } else {
            nombreRangoArchivo = String.valueOf(carta.getRango().getValor());
        }

        sb.append(nombreRangoArchivo).append("_").append(nombrePaloCarpeta).append(".png");

        return sb.toString();
    }

    public void seleccionar(boolean s) {
        if (bordeSeleccion != null) { // Asegurarse de que el borde exista
            bordeSeleccion.setVisible(s);
        }
    }

    public Carta getCarta() {
        return carta;
    }
}