// ConstructorTablero.java
package utilidades;

import modelo.*;

/**
 * ConcreteBuilder del patrón GoF Builder: implementa los pasos de
 * {@link ConstructorDeTablero} construyendo y cableando el {@link Tablero}.
 * Usa la {@link FabricaDeEntidades} (Simple Factory + Singleton) para instanciar
 * cada celda y registra las entidades en los subsistemas Observer del tablero.
 */
public class ConstructorTablero implements ConstructorDeTablero {

    private Tablero tablero;
    private final FabricaDeEntidades fabrica;

    public ConstructorTablero() {
        this.fabrica = FabricaDeEntidades.getInstancia();
        reiniciar();
    }

    @Override
    public void reiniciar() {
        this.tablero = new Tablero();
    }

    @Override
    public void definirTamanio(int filas, int columnas) {
        tablero.inicializarGrilla(filas, columnas);
    }

    @Override
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
     * registra cerrojos y muros en el gestor de canales y destinos en el gestor
     * de victoria (cableado del patron Observer) y fija la posicion inicial del
     * jugador.
     */
    private void registrarSegunTipo(Entidad entidad, int fila, int columna) {
        GestorDeCanales gestor = tablero.getGestorDeCanales();
        if (entidad instanceof CasilleroCerrojo) {
            gestor.registrarCerrojo((CasilleroCerrojo) entidad);
        } else if (entidad instanceof Muro) {
            gestor.registrarMuro((Muro) entidad);
        } else if (entidad instanceof Destino destino) {
            tablero.getGestorDeVictoria().registrarDestino(destino);
        } else if (entidad instanceof CintaTransportadora) {
            tablero.getGestorDeCintas().registrar(fila, columna);
        } else if (entidad instanceof Sokoban sokoban) {
            tablero.setPosicionJugador(fila, columna);
            tablero.setJugador(sokoban);
        }
    }

    @Override
    public Tablero obtenerTablero() {
        return this.tablero;
    }
}
