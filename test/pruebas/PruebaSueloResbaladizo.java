package pruebas;

import modelo.*;

/**
 * Prueba del patrón Strategy aplicado al piso resbaladizo.
 *
 * Semántica "solo sobre el hielo": la entidad se desliza mientras pisa hielo y
 * se detiene al abandonarlo (primer suelo normal) o al chocar contra algo.
 *
 * Escenarios:
 *  1. El jugador se desliza por el hielo y frena contra una pared.
 *  2. El jugador frena en el primer suelo normal (abandona el hielo).
 *  3. Una caja empujada al hielo se desliza sola.
 *  4. El deslizamiento frena al chocar contra una caja.
 *  5. Memento: el undo revierte el deslizamiento completo.
 */
public class PruebaSueloResbaladizo {

    private static int fallos = 0;

    public static void main(String[] args) {
        escenarioDeslizaHastaPared();
        escenarioFrenaAlSalirDelHielo();
        escenarioCajaSeDesliza();
        escenarioFrenaContraCaja();
        escenarioUndoRevierteDeslizamiento();

        System.out.println();
        if (fallos == 0) {
            System.out.println("TODAS LAS PRUEBAS DE SUELO RESBALADIZO PASARON");
        } else {
            System.out.println(fallos + " PRUEBA(S) FALLARON");
            System.exit(1);
        }
    }

    // ── 1. Desliza hasta la pared ─────────────────────────────────────────────

    private static void escenarioDeslizaHastaPared() {
        // Fila 1:  P J R R R R P   (cols 2..5 hielo, col 6 pared del borde)
        Tablero tablero = base(3, 7);
        Sokoban jugador = ponerJugador(tablero, 1, 1, Direccion.DERECHA);
        for (int c = 2; c <= 5; c++) tablero.colocarElemento(1, c, new SueloResbaladizo());

        MotorMovimiento motor = new MotorMovimiento(tablero);
        motor.intentarMover(Direccion.DERECHA);   // entra al hielo y se desliza

        check("HastaPared: el jugador se deslizó hasta la última celda antes de la pared",
                tablero.getJugadorColumna() == 5);
        check("HastaPared: la celda de hielo final lo contiene",
                tablero.getOcupante(1, 5) == jugador);
    }

    // ── 2. Frena al salir del hielo ───────────────────────────────────────────

    private static void escenarioFrenaAlSalirDelHielo() {
        // Fila 1:  P J R R S S P   (cols 2,3 hielo; cols 4,5 suelo normal)
        Tablero tablero = base(3, 7);
        Sokoban jugador = ponerJugador(tablero, 1, 1, Direccion.DERECHA);
        tablero.colocarElemento(1, 2, new SueloResbaladizo());
        tablero.colocarElemento(1, 3, new SueloResbaladizo());

        MotorMovimiento motor = new MotorMovimiento(tablero);
        motor.intentarMover(Direccion.DERECHA);

        check("SaleDelHielo: frena en el primer suelo normal (col 4)",
                tablero.getJugadorColumna() == 4);
    }

    // ── 3. Caja empujada al hielo se desliza ──────────────────────────────────

    private static void escenarioCajaSeDesliza() {
        // Fila 1:  P J C R R S P   (caja en col 2; hielo en cols 3,4; suelo en col 5)
        Tablero tablero = base(3, 7);
        Sokoban jugador = ponerJugador(tablero, 1, 1, Direccion.DERECHA);
        tablero.colocarElemento(1, 2, new CajaNormal());
        tablero.colocarElemento(1, 3, new SueloResbaladizo());
        tablero.colocarElemento(1, 4, new SueloResbaladizo());

        MotorMovimiento motor = new MotorMovimiento(tablero);
        motor.intentarMover(Direccion.DERECHA);   // empuja la caja al hielo

        check("CajaDesliza: la caja se deslizó hasta el primer suelo normal (col 5)",
                tablero.getOcupante(1, 5) instanceof CajaNormal);
        check("CajaDesliza: el jugador quedó en la celda original de la caja (col 2)",
                tablero.getJugadorColumna() == 2);
    }

    // ── 4. Frena contra una caja ──────────────────────────────────────────────

    private static void escenarioFrenaContraCaja() {
        // Fila 1:  P J R R R C P   (hielo en 2,3,4; caja en 5)
        Tablero tablero = base(3, 7);
        Sokoban jugador = ponerJugador(tablero, 1, 1, Direccion.DERECHA);
        for (int c = 2; c <= 4; c++) tablero.colocarElemento(1, c, new SueloResbaladizo());
        tablero.colocarElemento(1, 5, new CajaNormal());

        MotorMovimiento motor = new MotorMovimiento(tablero);
        motor.intentarMover(Direccion.DERECHA);

        check("FrenaContraCaja: el jugador frena en la celda previa a la caja (col 4)",
                tablero.getJugadorColumna() == 4);
        check("FrenaContraCaja: la caja no se movió (col 5)",
                tablero.getOcupante(1, 5) instanceof CajaNormal);
    }

    // ── 5. Undo revierte el deslizamiento ─────────────────────────────────────

    private static void escenarioUndoRevierteDeslizamiento() {
        Tablero tablero = base(3, 7);
        Sokoban jugador = ponerJugador(tablero, 1, 1, Direccion.DERECHA);
        for (int c = 2; c <= 5; c++) tablero.colocarElemento(1, c, new SueloResbaladizo());

        MotorMovimiento motor = new MotorMovimiento(tablero);
        TableroMemento snapshot = tablero.guardarEstado(0);

        motor.intentarMover(Direccion.DERECHA);
        check("Undo: el jugador se deslizó (col 5)", tablero.getJugadorColumna() == 5);

        tablero.restaurarEstado(snapshot);
        check("Undo: tras deshacer vuelve a la columna 1", tablero.getJugadorColumna() == 1);
        check("Undo: la celda de hielo (1,5) quedó vacía",
                tablero.getOcupante(1, 5) == null);
        check("Undo: el jugador está de nuevo en (1,1)",
                tablero.getOcupante(1, 1) == jugador);
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
