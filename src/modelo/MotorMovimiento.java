package modelo;

/**
 * Coordina las reglas de movimiento del jugador: mirada (giro vs. avance),
 * empuje de cajas y rotura de cajas frágiles.
 *
 * No usa {@code instanceof}: delega en métodos polimórficos de las entidades
 * ({@link Entidad#getOcupante()}, {@link EntidadDinamica#esEmpujable()},
 * {@link Caja#alSerEmpujada()}, {@link Caja#estaRota()}) y en los métodos de
 * grilla de {@link Tablero} ({@code colocarDinamica}, {@code quitarDinamica},
 * {@code moverDinamica}), que encapsulan el modelo de casillero intermedio.
 */
public class MotorMovimiento {

    private final Tablero tablero;

    public MotorMovimiento(Tablero tablero) {
        this.tablero = tablero;
    }

    public ResultadoMovimiento intentarMover(Direccion direccion) {
        Sokoban jugador = tablero.getJugador();

        // Regla de mirada: si el jugador no mira hacia allí, el primer toque solo
        // gira (no altera la matriz → no cuenta como movimiento ni genera snapshot).
        if (jugador.getMirada() != direccion) {
            jugador.setMirada(direccion);
            return ResultadoMovimiento.GIRO;
        }

        int filaOrigen    = tablero.getJugadorFila();
        int columnaOrigen = tablero.getJugadorColumna();
        int filaDestino   = filaOrigen + direccion.getDeltaFila();
        int columnaDestino= columnaOrigen + direccion.getDeltaColumna();

        if (!tablero.esPosicionValida(filaDestino, columnaDestino)) {
            return ResultadoMovimiento.SIN_CAMBIO;
        }

        EntidadDinamica ocupante = tablero.getOcupante(filaDestino, columnaDestino);
        if (ocupante != null && ocupante.esEmpujable()) {
            return empujarCaja(filaDestino, columnaDestino, direccion)
                    ? ResultadoMovimiento.MOVIMIENTO
                    : ResultadoMovimiento.SIN_CAMBIO;
        }

        if (!tablero.esTransitableEn(filaDestino, columnaDestino)) {
            return ResultadoMovimiento.SIN_CAMBIO;
        }

        tablero.moverDinamica(filaOrigen, columnaOrigen, filaDestino, columnaDestino);
        tablero.setPosicionJugador(filaDestino, columnaDestino);
        return ResultadoMovimiento.MOVIMIENTO;
    }

    private boolean empujarCaja(int filaCaja, int columnaCaja, Direccion direccion) {
        int filaDestino    = filaCaja + direccion.getDeltaFila();
        int columnaDestino = columnaCaja + direccion.getDeltaColumna();

        if (!tablero.esPosicionValida(filaDestino, columnaDestino)) return false;
        if (!tablero.celdaLibreParaDinamica(filaDestino, columnaDestino)) return false;

        EntidadDinamica caja = tablero.getOcupante(filaCaja, columnaCaja);
        if (caja == null) return false;

        // Empuje válido: la caja reacciona (la frágil pierde resistencia) y avanza.
        caja.alSerEmpujada();
        tablero.moverDinamica(filaCaja, columnaCaja, filaDestino, columnaDestino);

        // Si quedó rota tras el empuje, desaparece de la celda de destino.
        if (caja.estaRota()) {
            tablero.quitarDinamica(filaDestino, columnaDestino);
        }

        // El jugador ocupa la celda que dejó libre la caja.
        int filaJugador    = tablero.getJugadorFila();
        int columnaJugador = tablero.getJugadorColumna();
        tablero.moverDinamica(filaJugador, columnaJugador, filaCaja, columnaCaja);
        tablero.setPosicionJugador(filaCaja, columnaCaja);
        return true;
    }
}
