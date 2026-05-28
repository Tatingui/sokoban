package modelo;

public class Tablero {
    private Entidad[][] grilla;
    private int jugadorFila;
    private int jugadorColumna;

    public void inicializarGrilla(int filas, int columnas) {
        grilla = new Entidad[filas][columnas];
    }

    public void colocarElemento(int fila, int columna, Entidad entidad) {
        grilla[fila][columna] = entidad;
    }

    public void setPosicionJugador(int fila, int columna) {
        this.jugadorFila = fila;
        this.jugadorColumna = columna;
    }

    public int getJugadorFila() { return jugadorFila; }
    public int getJugadorColumna() { return jugadorColumna; }
    public Entidad[][] getGrilla() { return grilla; }
}