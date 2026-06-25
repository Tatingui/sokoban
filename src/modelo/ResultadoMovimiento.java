package modelo;

/**
 * Resultado de un intento de movimiento del jugador. Distingue los tres casos
 * que el controlador necesita tratar de forma diferente:
 *
 *  - {@link #GIRO}:        el jugador solo cambió su mirada (no se alteró la
 *                          matriz). Repaint del sprite, pero sin snapshot ni
 *                          conteo de movimiento.
 *  - {@link #MOVIMIENTO}:  el jugador avanzó (caminó o empujó una caja). Es un
 *                          movimiento válido: cuenta y genera snapshot de Memento.
 *  - {@link #SIN_CAMBIO}:  el intento quedó bloqueado (pared, caja inamovible,
 *                          borde). Nada cambió.
 */
public enum ResultadoMovimiento {
    GIRO,
    MOVIMIENTO,
    SIN_CAMBIO
}
