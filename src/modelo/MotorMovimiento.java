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

        ResultadoMovimiento resultado = avanzar(direccion);

        // Tras un movimiento válido, las cintas arrastran un paso a sus ocupantes.
        if (resultado.esAvance()) {
            tablero.getGestorDeCintas().avanzar(tablero);
        }
        return resultado;
    }

    private ResultadoMovimiento avanzar(Direccion direccion) {
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
                    ? ResultadoMovimiento.EMPUJE
                    : ResultadoMovimiento.SIN_CAMBIO;
        }

        if (!tablero.esTransitableEn(filaDestino, columnaDestino)) {
            // Bloqueado por pared: si hay un portal en esta arista, se teletransporta.
            return teletransportar(filaOrigen, columnaOrigen, direccion);
        }

        tablero.moverDinamica(filaOrigen, columnaOrigen, filaDestino, columnaDestino);
        Posicion fin = deslizarSiCorresponde(filaDestino, columnaDestino, direccion);
        tablero.setPosicionJugador(fin.fila(), fin.columna());
        return ResultadoMovimiento.CAMINATA;
    }

    private ResultadoMovimiento teletransportar(int fila, int columna, Direccion direccion) {
        Portal salida = tablero.getGestorDePortales().salidaDesde(fila, columna, direccion);
        if (salida == null) return ResultadoMovimiento.SIN_CAMBIO;

        int filaSalida    = salida.fila();
        int columnaSalida = salida.columna();
        
        // Según lo solicitado: Si hay una caja ocupando la salida del portal,
        // directamente no permitimos el teletransporte para evitar comportamientos anómalos.
        if (!tablero.celdaLibreParaDinamica(filaSalida, columnaSalida)) {
            return ResultadoMovimiento.SIN_CAMBIO;
        }

        Direccion salidaDir = salida.lado().getOpuesta();   // hacia afuera de la pared

        tablero.moverDinamica(fila, columna, filaSalida, columnaSalida);
        tablero.setPosicionJugador(filaSalida, columnaSalida);
        tablero.getJugador().setMirada(salidaDir);
        return ResultadoMovimiento.CAMINATA;
    }

    /**
     * Si en (fila, columna) hay un portal hacia {@code direccion} con par y su
     * celda de salida está libre, devuelve esa celda; si no, {@code null}.
     */
    private Posicion salidaPortal(int fila, int columna, Direccion direccion) {
        Portal salida = tablero.getGestorDePortales().salidaDesde(fila, columna, direccion);
        if (salida == null) return null;
        if (!tablero.celdaLibreParaDinamica(salida.fila(), salida.columna())) return null;
        return new Posicion(salida.fila(), salida.columna());
    }

    private boolean empujarCaja(int filaCaja, int columnaCaja, Direccion direccion) {
        int filaAdyacente    = filaCaja + direccion.getDeltaFila();
        int columnaAdyacente = columnaCaja + direccion.getDeltaColumna();

        // Destino de la caja: la celda contigua si está libre, o la salida de un
        // portal si la contigua es una pared con un portal en esa arista.
        Posicion destinoCaja;
        boolean porPortal;
        if (tablero.esPosicionValida(filaAdyacente, columnaAdyacente)
                && tablero.celdaLibreParaDinamica(filaAdyacente, columnaAdyacente)) {
            destinoCaja = new Posicion(filaAdyacente, columnaAdyacente);
            porPortal   = false;
        } else {
            destinoCaja = salidaPortal(filaCaja, columnaCaja, direccion);
            if (destinoCaja == null) return false;
            porPortal   = true;
        }

        EntidadDinamica caja = tablero.getOcupante(filaCaja, columnaCaja);
        if (caja == null) return false;

        // Empuje válido: la caja reacciona (la frágil pierde resistencia) y avanza.
        caja.alSerEmpujada();
        tablero.moverDinamica(filaCaja, columnaCaja, destinoCaja.fila(), destinoCaja.columna());

        if (caja.estaRota()) {
            // Si quedó rota tras el empuje, desaparece de la celda de destino.
            tablero.quitarDinamica(destinoCaja.fila(), destinoCaja.columna());
        } else if (!porPortal) {
            // La caja también se desliza si cae en hielo (no al salir de un portal).
            deslizarSiCorresponde(destinoCaja.fila(), destinoCaja.columna(), direccion);
        }

        // El jugador ocupa la celda que dejó libre la caja (y desliza si es hielo).
        int filaJugador    = tablero.getJugadorFila();
        int columnaJugador = tablero.getJugadorColumna();
        tablero.moverDinamica(filaJugador, columnaJugador, filaCaja, columnaCaja);
        Posicion fin = deslizarSiCorresponde(filaCaja, columnaCaja, direccion);
        tablero.setPosicionJugador(fin.fila(), fin.columna());
        return true;
    }

    /**
     * Aplica la estrategia del suelo donde quedó la entidad (patrón Strategy):
     * el suelo normal no hace nada; el resbaladizo la desliza. Devuelve la
     * posición final de reposo.
     */
    private Posicion deslizarSiCorresponde(int fila, int columna, Direccion direccion) {
        return tablero.estrategiaSueloEn(fila, columna)
                .alLlegar(tablero, fila, columna, direccion);
    }
}
