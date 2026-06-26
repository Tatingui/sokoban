package modelo;

/**
 * Estrategia de la cinta transportadora (Concrete Strategy): al resolverse un
 * turno, arrastra la entidad que tenga encima una celda en la dirección de la
 * cinta, si la celda de destino está libre.
 *
 * Lleva su propia dirección, de modo que existe una instancia por sentido de
 * cinta (la crea cada {@link CintaTransportadora} con su dirección).
 */
public final class ArrastreCinta implements EstrategiaDeSuelo {

    private final Direccion direccion;

    public ArrastreCinta(Direccion direccion) {
        this.direccion = direccion;
    }

    @Override
    public void alResolverTurno(Tablero tablero, int fila, int columna) {
        EntidadDinamica ocupante = tablero.getOcupante(fila, columna);
        if (ocupante == null) return;

        int filaDestino    = fila + direccion.getDeltaFila();
        int columnaDestino = columna + direccion.getDeltaColumna();

        if (tablero.esPosicionValida(filaDestino, columnaDestino)
                && tablero.celdaLibreParaDinamica(filaDestino, columnaDestino)) {
            tablero.moverDinamica(fila, columna, filaDestino, columnaDestino);
            if (ocupante.esJugador()) {
                tablero.setPosicionJugador(filaDestino, columnaDestino);
            }
        }
    }
}
