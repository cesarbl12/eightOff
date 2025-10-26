package modelo.estructuras;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ¡PLANTILLA! Reemplaza esta clase con tu propia implementación de ListaDoble.
 * Mantiene un cursor interno para UNDO/REDO.
 */
public class ListaDoble<T> {

    // --- Simulación con LinkedList ---
    private LinkedList<T> lista;
    private int cursor; // -1: antes del inicio, 0: primer elemento, ..., size-1: último

    public ListaDoble() {
        lista = new LinkedList<>();
        cursor = -1; // Empieza antes del primer elemento
    }

    // --- Métodos Requeridos por el Juego ---

    /**
     * Agrega un dato DESPUÉS de la posición actual del cursor.
     * Elimina todos los elementos que estaban después (historial REDO).
     * El cursor avanza a la nueva posición.
     */
    public void agregar(T dato) {
        // Eliminar historial futuro (REDO)
        truncarDesdeCursor(); // Usa el nuevo método para limpiar
        // Agregar el nuevo elemento
        lista.add(dato);
        cursor++; // Avanzar cursor al nuevo elemento
    }

    /** Mueve el cursor hacia atrás. Devuelve true si se movió. */
    public boolean retroceder() {
        if (!estaEnInicio()) { // Usar método helper
            cursor--;
            return true;
        }
        return false;
    }

    /** Mueve el cursor hacia adelante. Devuelve true si se movió. */
    public boolean avanzar() {
        if (!estaEnFinal()) { // Usar método helper
            cursor++;
            return true;
        }
        return false;
    }

    /** Devuelve el elemento en la posición actual del cursor. Null si está al inicio. */
    public T getActual() {
        if (!estaEnInicio() && cursor < lista.size()) { // Asegurar índice válido
            return lista.get(cursor);
        }
        return null; // Antes del primer movimiento
    }

    /** Devuelve el elemento SIGUIENTE al cursor (para REDO). Null si no hay. */
    public T getSiguiente() {
        int siguienteIndice = cursor + 1;
        if (siguienteIndice >= 0 && siguienteIndice < lista.size()) {
            return lista.get(siguienteIndice);
        }
        return null;
    }

    /** Mueve el cursor al inicio (antes del primer elemento). */
    public void irAlInicio() {
        cursor = -1;
    }

    /** Mueve el cursor al final (último elemento añadido). */
    public void irAlFinal() {
        cursor = lista.size() - 1;
    }

    /** Verifica si el cursor está antes del primer elemento. */
    public boolean estaEnInicio() {
        return cursor == -1;
    }

    /** Verifica si el cursor está en el último elemento. */
    public boolean estaEnFinal() {
        // Correcto incluso si la lista está vacía (cursor=-1, size=0 -> -1 != -1 es false)
        return cursor == lista.size() - 1;
    }

    /** Devuelve los elementos DESDE el inicio HASTA el cursor actual. */
    public List<T> getComoListaJava() {
        if (estaEnInicio()) {
            return new LinkedList<>(); // Vacía si está al inicio
        }
        // subList es inclusivo del inicio, exclusivo del final
        return lista.subList(0, cursor + 1);
    }

    /** Devuelve TODOS los elementos para visualización completa */
    public List<T> getTodosLosElementos() {
        return new LinkedList<>(lista);
    }

    /** Devuelve el índice actual del cursor (para UI) */
    public int getPosicionCursor() {
        return cursor;
    }

    // --- ¡NUEVO MÉTODO! ---
    /**
     * Elimina todos los elementos que están DESPUÉS del índice
     * apuntado actualmente por el cursor.
     * (Versión para el Stub con LinkedList)
     */
    public void truncarDesdeCursor() {
        // Los elementos a mantener son de 0 hasta cursor (inclusive)
        // El tamaño deseado es cursor + 1
        int indiceDespuesCursor = cursor + 1;
        // Eliminar desde el final hasta que el tamaño sea el correcto
        while (lista.size() > indiceDespuesCursor) {
            lista.removeLast();
        }
        // El cursor ya está en la posición correcta (el último elemento restante)
    }
}