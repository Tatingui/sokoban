package pruebas;

import modelo.*;

/**
 * Prueba del patrón Strategy aplicado a la cinta transportadora (efecto
 * "al resolverse el turno") y su coordinación por el GestorDeCintas.
 *
 * Escenarios:
 *  1. Una caja sobre la cinta avanza una celda por turno.
 *  2. Si la celda de destino está bloqueada, la caja no se mueve.
 *  3. Solo avanza UNA celda por turno aunque haya cintas consecutivas.
 *  4. El jugador sobre la cinta es arrastrado y se actualiza su posición.
 *  5. Integración con el motor: caminar a una cinta te arrastra en el mismo turno.
 *  6. Memento: el undo revierte el arrastre.
 */
public class PruebaCintaTransportadora {

    private static int fallos = 0;

    public static void main(String[] args) {
        escenarioCajaAvanzaUnPaso();
        escenarioBloqueadaNoAvanza();
        escenarioUnPasoPorTurno();
        escenarioJugadorArrastrado();
        escenarioIntegracionMotor();
        escenarioUndoRevierteArrastre();

        System.out.println();
        if (fallos == 0) {
            System.out.println("TODAS LAS PRUEBAS DE CINTA TRANSPORTADORA PASARON");
        } else {
            System.out.println(fallos + " PRUEBA(S) FALLARON");
            System.exit(1);
        }
    }

    // ── 1. Caja avanza un paso ────────────────────────────────────────────────

    private static void escenarioCajaAvanzaUnPaso() {
        Tablero tablero = base(3, 6);
        ponerCinta(tablero, 1, 2, Direccion.DERECHA);
        ponerEncima(tablero, 1, 2, new CajaNormal());

        tablero.getGestorDeCintas().avanzar(tablero);

        check("AvanzaUnPaso: la caja se movió a la celda siguiente (1,3)",
                tablero.getOcupante(1, 3) instanceof CajaNormal);
        check("AvanzaUnPaso: la cinta quedó libre (1,2)",
                tablero.getOcupante(1, 2) == null);
    }

    // ── 2. Bloqueada por pared ────────────────────────────────────────────────

    private static void escenarioBloqueadaNoAvanza() {
        Tablero tablero = base(3, 4);
        ponerCinta(tablero, 1, 2, Direccion.DERECHA);   // a la derecha está la pared del borde (col 3)
        ponerEncima(tablero, 1, 2, new CajaNormal());

        tablero.getGestorDeCintas().avanzar(tablero);

        check("Bloqueada: la caja no se mueve contra la pared",
                tablero.getOcupante(1, 2) instanceof CajaNormal);
    }

    // ── 3. Un paso por turno con cintas consecutivas ──────────────────────────

    private static void escenarioUnPasoPorTurno() {
        Tablero tablero = base(3, 7);
        ponerCinta(tablero, 1, 2, Direccion.DERECHA);
        ponerCinta(tablero, 1, 3, Direccion.DERECHA);
        ponerCinta(tablero, 1, 4, Direccion.DERECHA);
        ponerEncima(tablero, 1, 2, new CajaNormal());

        tablero.getGestorDeCintas().avanzar(tablero);

        check("UnPaso: tras un turno la caja avanzó exactamente una celda (1,3)",
                tablero.getOcupante(1, 3) instanceof CajaNormal
                        && tablero.getOcupante(1, 4) == null);

        tablero.getGestorDeCintas().avanzar(tablero);
        check("UnPaso: en el segundo turno avanza otra celda (1,4)",
                tablero.getOcupante(1, 4) instanceof CajaNormal);
    }

    // ── 4. Jugador arrastrado ─────────────────────────────────────────────────

    private static void escenarioJugadorArrastrado() {
        Tablero tablero = base(3, 6);
        ponerCinta(tablero, 1, 2, Direccion.DERECHA);
        Sokoban jugador = new Sokoban();
        ponerEncima(tablero, 1, 2, jugador);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 2);

        tablero.getGestorDeCintas().avanzar(tablero);

        check("JugadorArrastrado: el jugador fue movido a (1,3)",
                tablero.getOcupante(1, 3) == jugador);
        check("JugadorArrastrado: la posición rastreada se actualizó",
                tablero.getJugadorColumna() == 3);
    }

    // ── 5. Integración con el motor ───────────────────────────────────────────

    private static void escenarioIntegracionMotor() {
        // Fila 1:  P J T-DERECHA S S P  → camina a la cinta y la cinta lo arrastra 1 más
        Tablero tablero = base(3, 6);
        Sokoban jugador = new Sokoban();
        jugador.setMirada(Direccion.DERECHA);
        tablero.colocarElemento(1, 1, jugador);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);
        ponerCinta(tablero, 1, 2, Direccion.DERECHA);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        motor.intentarMover(Direccion.DERECHA);   // pisa la cinta (1,2) y es arrastrado a (1,3)

        check("Integracion: caminar a la cinta arrastra al jugador hasta (1,3)",
                tablero.getJugadorColumna() == 3 && tablero.getOcupante(1, 3) == jugador);
    }

    // ── 6. Undo revierte el arrastre ──────────────────────────────────────────

    private static void escenarioUndoRevierteArrastre() {
        Tablero tablero = base(3, 6);
        ponerCinta(tablero, 1, 2, Direccion.DERECHA);
        CajaNormal caja = new CajaNormal();
        ponerEncima(tablero, 1, 2, caja);

        TableroMemento snapshot = tablero.guardarEstado(0);
        tablero.getGestorDeCintas().avanzar(tablero);
        check("Undo: la caja se arrastró a (1,3)", tablero.getOcupante(1, 3) instanceof CajaNormal);

        tablero.restaurarEstado(snapshot);
        check("Undo: tras deshacer la caja vuelve a la cinta (1,2)",
                tablero.getOcupante(1, 2) instanceof CajaNormal);
        check("Undo: la celda (1,3) quedó libre", tablero.getOcupante(1, 3) == null);
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

    private static void ponerCinta(Tablero tablero, int fila, int columna, Direccion direccion) {
        tablero.colocarElemento(fila, columna, new CintaTransportadora(direccion));
        tablero.getGestorDeCintas().registrar(fila, columna);
    }

    private static void ponerEncima(Tablero tablero, int fila, int columna, EntidadDinamica dinamica) {
        tablero.colocarDinamica(fila, columna, dinamica);
    }

    private static void check(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[OK]   " : "[FALLA] ") + descripcion);
        if (!condicion) fallos++;
    }
}
