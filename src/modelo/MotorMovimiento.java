package modelo;

/**
 * Encapsula las reglas de desplazamiento del jugador y el empuje de cajas,
 * respetando el modelo compuesto de {@link SueloEspecial} y el Observer de
 * {@link CasilleroCerrojo}.
 */
public class MotorMovimiento {

    private final Tablero tablero;

    public MotorMovimiento(Tablero tablero) {
        this.tablero = tablero;
    }

    public boolean intentarMover(Direccion direccion) {
        int filaOrigen = tablero.getJugadorFila();
        int columnaOrigen = tablero.getJugadorColumna();
        int filaDestino = filaOrigen + direccion.getDeltaFila();
        int columnaDestino = columnaOrigen + direccion.getDeltaColumna();

        if (!tablero.esPosicionValida(filaDestino, columnaDestino)) {
            return false;
        }

        EntidadDinamica dinamicaDestino = obtenerDinamica(filaDestino, columnaDestino);
        if (dinamicaDestino instanceof Caja) {
            return intentarEmpujarCaja(filaDestino, columnaDestino, direccion);
        }

        if (!celdaTransitableParaJugador(filaDestino, columnaDestino)) {
            return false;
        }

        moverJugador(filaOrigen, columnaOrigen, filaDestino, columnaDestino);
        return true;
    }

    private boolean intentarEmpujarCaja(int filaCaja, int columnaCaja, Direccion direccion) {
        int filaDestinoCaja = filaCaja + direccion.getDeltaFila();
        int columnaDestinoCaja = columnaCaja + direccion.getDeltaColumna();

        if (!tablero.esPosicionValida(filaDestinoCaja, columnaDestinoCaja)) {
            return false;
        }

        if (!celdaLibreParaDinamica(filaDestinoCaja, columnaDestinoCaja)) {
            return false;
        }

        EntidadDinamica caja = obtenerDinamica(filaCaja, columnaCaja);
        if (caja == null) {
            return false;
        }

        colocarDinamica(filaDestinoCaja, columnaDestinoCaja, caja);
        quitarDinamica(filaCaja, columnaCaja);

        int filaJugador = tablero.getJugadorFila();
        int columnaJugador = tablero.getJugadorColumna();
        moverJugador(filaJugador, columnaJugador, filaCaja, columnaCaja);
        return true;
    }

    private void moverJugador(int filaOrigen, int columnaOrigen, int filaDestino, int columnaDestino) {
        Sokoban jugador = tablero.getJugador();
        quitarDinamica(filaOrigen, columnaOrigen);
        colocarDinamica(filaDestino, columnaDestino, jugador);
        tablero.setPosicionJugador(filaDestino, columnaDestino);
    }

    private EntidadDinamica obtenerDinamica(int fila, int columna) {
        Entidad entidad = tablero.getEntidad(fila, columna);
        if (entidad instanceof SueloEspecial sueloEspecial && sueloEspecial.estaOcupado()) {
            return sueloEspecial.getObjetoEncima();
        }
        if (entidad instanceof EntidadDinamica dinamica) {
            return dinamica;
        }
        return null;
    }

    private boolean celdaTransitableParaJugador(int fila, int columna) {
        Entidad entidad = tablero.getEntidad(fila, columna);
        if (entidad == null) {
            return true;
        }
        if (entidad instanceof SueloEspecial sueloEspecial) {
            return !sueloEspecial.estaOcupado();
        }
        return entidad.esTransitable();
    }

    private boolean celdaLibreParaDinamica(int fila, int columna) {
        Entidad entidad = tablero.getEntidad(fila, columna);
        if (entidad == null) {
            return true;
        }
        if (entidad instanceof SueloEspecial sueloEspecial) {
            return !sueloEspecial.estaOcupado();
        }
        return entidad.esTransitable() && !(entidad instanceof EntidadDinamica);
    }

    private void colocarDinamica(int fila, int columna, EntidadDinamica dinamica) {
        Entidad entidad = tablero.getEntidad(fila, columna);
        if (entidad instanceof SueloEspecial sueloEspecial) {
            sueloEspecial.setObjetoEncima(dinamica);
            return;
        }
        tablero.colocarElemento(fila, columna, dinamica);
    }

    private void quitarDinamica(int fila, int columna) {
        Entidad entidad = tablero.getEntidad(fila, columna);
        if (entidad instanceof SueloEspecial sueloEspecial) {
            sueloEspecial.setObjetoEncima(null);
            return;
        }
        if (entidad instanceof Sokoban || entidad instanceof Caja) {
            tablero.colocarElemento(fila, columna, new SueloNormal());
        }
    }
}
