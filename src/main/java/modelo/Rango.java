package modelo;

public enum Rango {
    AS(1, "A"),
    DOS(2, "2"),
    TRES(3, "3"),
    CUATRO(4, "4"),
    CINCO(5, "5"),
    SEIS(6, "6"),
    SIETE(7, "7"),
    OCHO(8, "8"),
    NUEVE(9, "9"),
    DIEZ(10, "10"),
    JOTA(11, "J"),
    REINA(12, "Q"),
    REY(13, "K");

    private final int valor;
    private final String display;

    Rango(int valor, String display) {
        this.valor = valor;
        this.display = display;
    }

    public int getValor() { return valor; }
    public String getDisplay() { return display; }
}