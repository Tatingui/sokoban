package modelo;

/**
 * Resultado de un intento de movimiento del jugador. Distingue los casos que el
 * controlador necesita tratar de forma diferente:
 *
 *  - {@link #GIRO}:       el jugador solo cambió su mirada (no se alteró la
 *                         matriz). Repaint del sprite, pero sin snapshot ni conteo.
 *  - {@link #CAMINATA}:   el jugador avanzó a una celda libre sin empujar nada.
 *  - {@link #EMPUJE}:     el jugador avanzó empujando una caja (cuenta además
 *                         como empuje efectuado).
 *  - {@link #SIN_CAMBIO}: el intento quedó bloqueado (pared, caja inamovible,
 *                         borde). Nada cambió.
 *
 * {@link #CAMINATA} y {@link #EMPUJE} son "avances": cuentan como movimiento y
 * generan snapshot de Memento ({@link #esAvance()}).
 */
public enum ResultadoMovimiento {
    GIRO,
    CAMINATA,
    EMPUJE,
    SIN_CAMBIO;

    /** ¿Hubo un avance real (caminar o empujar)? */
    public boolean esAvance() {
        return this == CAMINATA || this == EMPUJE;
    }
}
