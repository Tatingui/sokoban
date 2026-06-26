package modelo;

/**
 * Estrategia del piso resbaladizo (Concrete Strategy): la entidad que cae en el
 * hielo sigue desplazándose en la misma dirección.
 *
 * Semántica "solo sobre el hielo": avanza de a una celda y delega en la
 * estrategia del suelo siguiente:
 *  - si la celda siguiente vuelve a ser hielo, ésta delega en sí misma → sigue;
 *  - si es un suelo normal, su {@link SueloFijo} la deja ahí → abandona el hielo;
 *  - si está bloqueada (pared, caja, borde), se detiene en la celda actual.
 *
 * Es stateless, así que se comparte una única instancia.
 */
public final class Deslizamiento implements EstrategiaDeSuelo {

    public static final Deslizamiento INSTANCIA = new Deslizamiento();

    private Deslizamiento() { }

    @Override
    public Posicion alLlegar(Tablero tablero, int fila, int columna, Direccion direccion) {
        int filaSiguiente    = fila + direccion.getDeltaFila();
        int columnaSiguiente = columna + direccion.getDeltaColumna();

        // No puede seguir (borde, pared o caja): reposa en la celda de hielo actual.
        if (!tablero.esPosicionValida(filaSiguiente, columnaSiguiente)
                || !tablero.celdaLibreParaDinamica(filaSiguiente, columnaSiguiente)) {
            return new Posicion(fila, columna);
        }

        // Avanza una celda y deja que el suelo de destino decida la continuación.
        tablero.moverDinamica(fila, columna, filaSiguiente, columnaSiguiente);
        return tablero.estrategiaSueloEn(filaSiguiente, columnaSiguiente)
                .alLlegar(tablero, filaSiguiente, columnaSiguiente, direccion);
    }
}
