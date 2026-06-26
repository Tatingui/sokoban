package modelo;

/**
 * Calcula el puntaje de un nivel en función del esfuerzo del jugador: a más
 * tiempo, más movimientos y más usos del botón de deshacer, menor puntaje.
 *
 * Fórmula: se parte de un puntaje base y se descuenta una penalización por cada
 * variable; nunca baja de 0. Es una función pura (sin estado), usada tanto por
 * el HUD para mostrar el puntaje en vivo como por la pantalla de victoria.
 */
public final class CalculadoraDePuntaje {

    private static final int PUNTAJE_BASE   = 10_000;
    private static final int PESO_SEGUNDO   = 5;    // cada segundo resta 5
    private static final int PESO_MOVIMIENTO = 20;  // cada movimiento resta 20
    private static final int PESO_DESHACER  = 100;  // cada uso de deshacer resta 100

    private CalculadoraDePuntaje() { }

    public static int calcular(int segundos, int movimientos, int deshacerUsados) {
        int puntaje = PUNTAJE_BASE
                - segundos      * PESO_SEGUNDO
                - movimientos   * PESO_MOVIMIENTO
                - deshacerUsados * PESO_DESHACER;
        return Math.max(0, puntaje);
    }
}
