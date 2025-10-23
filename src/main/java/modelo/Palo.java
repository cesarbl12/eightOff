package modelo;

public enum Palo {
    PICAS("♠", Color.NEGRO),
    CORAZONES("♥", Color.ROJO),
    DIAMANTES("♦", Color.ROJO),
    TREBOLES("♣", Color.NEGRO);

    public enum Color { ROJO, NEGRO }

    private final String simbolo;
    private final Color color;

    Palo(String simbolo, Color color) {
        this.simbolo = simbolo;
        this.color = color;
    }

    public String getSimbolo() { return simbolo; }
    public Color getColor() { return color; }
}