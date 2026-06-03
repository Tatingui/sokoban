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
            registrarSegunTipo(entidad, fila, columna);
        }
    }

    /**
     * Conecta la entidad recien creada con los subsistemas del tablero:
     * registra cerrojos y muros en el gestor de canales (cableado del patron
     * Observer) y fija la posicion inicial del jugador.
     */
    private void registrarSegunTipo(Entidad entidad, int fila, int columna) {
        GestorDeCanales gestor = tablero.getGestorDeCanales();
        if (entidad instanceof CasilleroCerrojo) {
            gestor.registrarCerrojo((CasilleroCerrojo) entidad);
        } else if (entidad instanceof Muro) {
            gestor.registrarMuro((Muro) entidad);
        } else if (entidad instanceof Sokoban) {
            tablero.setPosicionJugador(fila, columna);
        }
    }

    public Tablero obtenerTablero() {
        return this.tablero;
    }
}