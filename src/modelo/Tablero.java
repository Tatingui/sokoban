package modelo;

public class Tablero {
    private ElementoTablero[][] grilla;
    private int jugadorX;
    private int jugadorY;

    // El tablero se inicializa vacío con un tamaño específico
    public void inicializarGrilla(int filas, int columnas) {
        grilla = new ElementoTablero[filas][columnas];
    }

    public void colocarElemento(int f, int c, ElementoTablero elemento) {
        grilla[f][c] = elemento;
    }

    public void setPosicionJugador(int x, int y) {
        this.jugadorX = x;
        this.jugadorY = y;
    }

    // Getters básicos
    public ElementoTablero[][] getGrilla() {
        return grilla;
    }
}