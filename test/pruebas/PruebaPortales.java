package pruebas;

import modelo.*;

/**
 * Prueba de los portales: el disparo (ray-cast que atraviesa cajas y frena solo
 * contra paredes) y el teletransporte de Sokoban y de las cajas empujadas,
 * coordinados por el GestorDePortales.
 *
 * Escenarios:
 *  1. El disparo atraviesa una caja y queda en la última celda antes de la pared.
 *  2. Sokoban se teletransporta al entrar al portal hacia la pared.
 *  3. Una caja empujada contra el portal sale por el otro.
 *  4. Sin par, no hay teletransporte (queda bloqueado).
 *  5. Memento: el undo revierte el teletransporte.
 */
public class PruebaPortales {

    private static int fallos = 0;

    public static void main(String[] args) {
        escenarioDisparoAtraviesaCaja();
        escenarioJugadorSeTeletransporta();
        escenarioCajaSeTeletransporta();
        escenarioSalidaOcupadaBloquea();
        escenarioSinParNoTeletransporta();
        escenarioUndoRevierteTeletransporte();

        System.out.println();
        if (fallos == 0) {
            System.out.println("TODAS LAS PRUEBAS DE PORTALES PASARON");
        } else {
            System.out.println(fallos + " PRUEBA(S) FALLARON");
            System.exit(1);
        }
    }

    // ── 1. El disparo atraviesa cajas y frena en la pared ─────────────────────

    private static void escenarioDisparoAtraviesaCaja() {
        // Fila 1:  P J S C S S P  → caja en (1,3); pared del borde en (1,6)
        Tablero tablero = base(3, 7);
        tablero.colocarElemento(1, 3, new CajaNormal());

        tablero.getGestorDePortales().disparar(tablero, ColorPortal.AZUL, 1, 1, Direccion.DERECHA);
        Portal azul = tablero.getGestorDePortales().getAzul();

        check("Disparo: el portal atravesó la caja y quedó antes de la pared (1,5)",
                azul != null && azul.fila() == 1 && azul.columna() == 5
                        && azul.lado() == Direccion.DERECHA);
    }

    // ── 2. Sokoban se teletransporta ──────────────────────────────────────────

    private static void escenarioJugadorSeTeletransporta() {
        Tablero tablero = base(5, 7);
        GestorDePortales portales = tablero.getGestorDePortales();
        // Portal azul a la derecha (fila 1) y naranja a la izquierda (fila 3).
        portales.disparar(tablero, ColorPortal.AZUL,    1, 1, Direccion.DERECHA);    // → (1,5) DERECHA
        portales.disparar(tablero, ColorPortal.NARANJA, 3, 5, Direccion.IZQUIERDA);  // → (3,1) IZQUIERDA

        Sokoban jugador = ponerJugador(tablero, 1, 5, Direccion.DERECHA);  // sobre el portal azul

        MotorMovimiento motor = new MotorMovimiento(tablero);
        motor.intentarMover(Direccion.DERECHA);   // entra al portal azul (hacia la pared)

        check("Jugador: se teletransportó a la celda del portal naranja (3,1)",
                tablero.getJugadorFila() == 3 && tablero.getJugadorColumna() == 1
                        && tablero.getOcupante(3, 1) == jugador);
        check("Jugador: emerge mirando hacia afuera de la pared (DERECHA)",
                jugador.getMirada() == Direccion.DERECHA);
        check("Jugador: la celda del portal azul quedó libre",
                tablero.getOcupante(1, 5) == null);
    }

    // ── 3. Una caja empujada se teletransporta ────────────────────────────────

    private static void escenarioCajaSeTeletransporta() {
        Tablero tablero = base(5, 7);
        GestorDePortales portales = tablero.getGestorDePortales();
        portales.disparar(tablero, ColorPortal.AZUL,    1, 1, Direccion.DERECHA);    // → (1,5) DERECHA
        portales.disparar(tablero, ColorPortal.NARANJA, 3, 5, Direccion.IZQUIERDA);  // → (3,1) IZQUIERDA

        Sokoban jugador = ponerJugador(tablero, 1, 4, Direccion.DERECHA);
        CajaNormal caja = new CajaNormal();
        tablero.colocarElemento(1, 5, caja);   // caja sobre el portal azul

        MotorMovimiento motor = new MotorMovimiento(tablero);
        motor.intentarMover(Direccion.DERECHA);   // empuja la caja contra el portal

        check("Caja: salió por el portal naranja (3,1)",
                tablero.getOcupante(3, 1) instanceof CajaNormal);
        check("Caja: el jugador ocupó la celda que dejó la caja (1,5)",
                tablero.getJugadorColumna() == 5 && tablero.getOcupante(1, 5) == jugador);
    }

    // ── 3b. Salida ocupada por una caja: el teletransporte queda bloqueado ─────

    private static void escenarioSalidaOcupadaBloquea() {
        Tablero tablero = base(5, 7);
        GestorDePortales portales = tablero.getGestorDePortales();
        portales.disparar(tablero, ColorPortal.AZUL,    1, 1, Direccion.DERECHA);    // → (1,5) DERECHA
        portales.disparar(tablero, ColorPortal.NARANJA, 3, 5, Direccion.IZQUIERDA);  // → (3,1) IZQUIERDA

        Sokoban jugador = ponerJugador(tablero, 1, 4, Direccion.DERECHA);
        tablero.colocarElemento(1, 5, new CajaNormal());   // caja sobre el portal azul

        MotorMovimiento motor = new MotorMovimiento(tablero);
        motor.intentarMover(Direccion.DERECHA);   // empuja la caja por el portal: caja→(3,1), jugador→(1,5)
        check("SalidaOcupada: tras empujar, el jugador quedó en la boca del portal (1,5)",
                tablero.getJugadorColumna() == 5 && tablero.getOcupante(3, 1) instanceof CajaNormal);

        // Intentar seguir: la salida (3,1) está ocupada por la caja → no teletransporta.
        ResultadoMovimiento r = motor.intentarMover(Direccion.DERECHA);
        check("SalidaOcupada: el segundo intento no teletransporta",
                r == ResultadoMovimiento.SIN_CAMBIO);
        check("SalidaOcupada: el jugador sigue en (1,5)",
                tablero.getJugadorColumna() == 5 && tablero.getOcupante(1, 5) == jugador);
        check("SalidaOcupada: la caja sigue en (3,1)",
                tablero.getOcupante(3, 1) instanceof CajaNormal);
    }

    // ── 4. Sin par no hay teletransporte ──────────────────────────────────────

    private static void escenarioSinParNoTeletransporta() {
        Tablero tablero = base(3, 7);
        tablero.getGestorDePortales().disparar(tablero, ColorPortal.AZUL, 1, 1, Direccion.DERECHA); // solo azul

        Sokoban jugador = ponerJugador(tablero, 1, 5, Direccion.DERECHA);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        motor.intentarMover(Direccion.DERECHA);   // contra la pared, sin portal par

        check("SinPar: el jugador no se movió (sigue en (1,5))",
                tablero.getJugadorColumna() == 5 && tablero.getOcupante(1, 5) == jugador);
    }

    // ── 5. Undo revierte el teletransporte ────────────────────────────────────

    private static void escenarioUndoRevierteTeletransporte() {
        Tablero tablero = base(5, 7);
        GestorDePortales portales = tablero.getGestorDePortales();
        portales.disparar(tablero, ColorPortal.AZUL,    1, 1, Direccion.DERECHA);
        portales.disparar(tablero, ColorPortal.NARANJA, 3, 5, Direccion.IZQUIERDA);

        Sokoban jugador = ponerJugador(tablero, 1, 5, Direccion.DERECHA);
        MotorMovimiento motor = new MotorMovimiento(tablero);
        TableroMemento snapshot = tablero.guardarEstado(0);

        motor.intentarMover(Direccion.DERECHA);
        check("Undo: el jugador se teletransportó a (3,1)", tablero.getJugadorFila() == 3);

        tablero.restaurarEstado(snapshot);
        check("Undo: vuelve a la celda del portal azul (1,5)",
                tablero.getJugadorFila() == 1 && tablero.getJugadorColumna() == 5
                        && tablero.getOcupante(1, 5) == jugador);
        check("Undo: la celda de salida (3,1) quedó libre",
                tablero.getOcupante(3, 1) == null);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private static Tablero base(int filas, int columnas) {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(filas, columnas);
        for (int f = 0; f < filas; f++)
            for (int c = 0; c < columnas; c++)
                tablero.colocarElemento(f, c,
                        (f == 0 || f == filas - 1 || c == 0 || c == columnas - 1)
                                ? new Pared() : new SueloNormal());
        return tablero;
    }

    private static Sokoban ponerJugador(Tablero tablero, int fila, int columna, Direccion mirada) {
        Sokoban jugador = new Sokoban();
        jugador.setMirada(mirada);
        tablero.colocarElemento(fila, columna, jugador);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(fila, columna);
        return jugador;
    }

    private static void check(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[OK]   " : "[FALLA] ") + descripcion);
        if (!condicion) fallos++;
    }
}
