// ConstructorTablero.java
package utilidades;

import modelo.*;

public class ConstructorTablero {

    private Tablero tablero;
    private final FabricaDeEntidades fabrica;

    public ConstructorTablero() {
        this.tablero = new Tablero();
        this.fabrica = FabricaDeEntidades.getInstancia();
    }

    public void definirTamanio(int filas, int columnas) {
        tablero.inicializarGrilla(filas, columnas);
    }

    public void procesarFila(int fila, String[] tokens) {
        for (int columna = 0; columna < tokens.length; columna++) {
            String token = tokens[columna].trim();
            Entidad entidad = fabrica.crearEntidad(token);
            tablero.colocarElemento(fila, columna, entidad);

            if (entidad instanceof Sokoban) {
                tablero.setPosicionJugador(fila, columna);
            }
        }
    }

    public Tablero obtenerTablero() {
        return this.tablero;
    }
}