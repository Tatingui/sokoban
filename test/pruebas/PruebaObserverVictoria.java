package pruebas;

import modelo.*;

/**
 * Prueba manual del patrón GoF Observer aplicado a la condición de victoria.
 *
 * Subject  = Destino (notifica al satisfacerse/insatisfacerse).
 * Observer = GestorDeVictoria (mantiene el contador en O(1) y decide la victoria).
 *
 * Escenarios cubiertos:
 *  1. Conteo: el gestor sube/baja al entrar y salir una caja del destino.
 *  2. Sólo cajas válidas: el Sokoban sobre un destino NO lo satisface.
 *  3. Victoria: nivelResuelto() pasa a true sólo con todos los destinos cubiertos.
 *  4. Estado inicial: una caja ya colocada al registrar el destino se cuenta.
 *  5. Memento: tras restaurar, recontar() deja el contador coherente.
 */
public class PruebaObserverVictoria {

    private static int fallos = 0;

    public static void main(String[] args) {
        escenarioConteoBasico();
        escenarioSokobanNoSatisface();
        escenarioLlaveNoSatisface();
        escenarioVictoriaConDosDestinos();
        escenarioEstadoInicialOcupado();
        escenarioRecuentoTrasMemento();

        System.out.println();
        if (fallos == 0) {
            System.out.println("TODAS LAS PRUEBAS DE OBSERVER VICTORIA PASARON");
        } else {
            System.out.println(fallos + " PRUEBA(S) FALLARON");
            System.exit(1);
        }
    }

    // ── 1. Conteo básico ─────────────────────────────────────────────────────

    private static void escenarioConteoBasico() {
        GestorDeVictoria gestor = new GestorDeVictoria();
        Destino destino = new Destino();
        gestor.registrarDestino(destino);

        check("Conteo: arranca en 0", gestor.getCajasEnDestino() == 0);

        destino.setObjetoEncima(new CajaNormal());
        check("Conteo: sube a 1 al entrar la caja", gestor.getCajasEnDestino() == 1);

        destino.setObjetoEncima(null);
        check("Conteo: baja a 0 al salir la caja", gestor.getCajasEnDestino() == 0);
    }

    // ── 2. Sokoban no satisface ──────────────────────────────────────────────

    private static void escenarioSokobanNoSatisface() {
        GestorDeVictoria gestor = new GestorDeVictoria();
        Destino destino = new Destino();
        gestor.registrarDestino(destino);

        destino.setObjetoEncima(new Sokoban());
        check("SokobanNoSatisface: el jugador no cuenta como caja válida",
                gestor.getCajasEnDestino() == 0 && !destino.estaSatisfecho());
    }

    // ── Una llave sobre un destino NO cuenta ─────────────────────────────────

    private static void escenarioLlaveNoSatisface() {
        GestorDeVictoria gestor = new GestorDeVictoria();
        Destino destino = new Destino();
        gestor.registrarDestino(destino);

        destino.setObjetoEncima(CajaLlave.deCanal("AZUL"));
        check("LlaveNoSatisface: una llave de canal no cuenta como caja en destino",
                gestor.getCajasEnDestino() == 0 && !destino.estaSatisfecho());

        destino.setObjetoEncima(CajaLlave.comodin());
        check("LlaveNoSatisface: una llave comodín tampoco cuenta",
                gestor.getCajasEnDestino() == 0 && !gestor.nivelResuelto());

        destino.setObjetoEncima(new CajaNormal());
        check("LlaveNoSatisface: una caja normal sí cuenta",
                gestor.getCajasEnDestino() == 1 && gestor.nivelResuelto());
    }

    // ── 3. Victoria con dos destinos ─────────────────────────────────────────

    private static void escenarioVictoriaConDosDestinos() {
        GestorDeVictoria gestor = new GestorDeVictoria();
        Destino d1 = new Destino();
        Destino d2 = new Destino();
        gestor.registrarDestino(d1);
        gestor.registrarDestino(d2);

        check("Victoria: aún no resuelto con 0/2", !gestor.nivelResuelto());

        d1.setObjetoEncima(new CajaNormal());
        check("Victoria: aún no resuelto con 1/2", !gestor.nivelResuelto());

        d2.setObjetoEncima(new CajaFragil(3));
        check("Victoria: resuelto con 2/2", gestor.nivelResuelto());

        d1.setObjetoEncima(null);
        check("Victoria: deja de estar resuelto al sacar una caja",
                !gestor.nivelResuelto());
    }

    // ── 4. Estado inicial ocupado ────────────────────────────────────────────

    private static void escenarioEstadoInicialOcupado() {
        Destino destino = new Destino();
        destino.setObjetoEncima(new CajaNormal());   // ya ocupado antes de registrar

        GestorDeVictoria gestor = new GestorDeVictoria();
        gestor.registrarDestino(destino);
        check("EstadoInicial: el destino ya ocupado se cuenta al registrar",
                gestor.getCajasEnDestino() == 1 && gestor.nivelResuelto());
    }

    // ── 5. Recuento tras Memento ─────────────────────────────────────────────

    private static void escenarioRecuentoTrasMemento() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(1, 4);
        Sokoban jugador = new Sokoban();
        CajaNormal caja = new CajaNormal();
        Destino destino = new Destino();

        tablero.colocarElemento(0, 0, jugador);
        tablero.colocarElemento(0, 1, caja);
        tablero.colocarElemento(0, 2, destino);
        tablero.colocarElemento(0, 3, new SueloNormal());  // espacio para sacar la caja
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(0, 0);
        jugador.setMirada(Direccion.DERECHA);    // ya mira a donde va a empujar
        tablero.getGestorDeVictoria().registrarDestino(destino);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        motor.intentarMover(Direccion.DERECHA);  // empuja la caja sobre el destino
        check("Memento: nivel resuelto tras empujar la caja al destino",
                tablero.nivelResuelto());

        TableroMemento snapResuelto = tablero.guardarEstado(1);
        motor.intentarMover(Direccion.DERECHA);  // saca la caja del destino
        check("Memento: ya no resuelto tras sacar la caja",
                !tablero.nivelResuelto());

        tablero.restaurarEstado(snapResuelto);
        check("Memento: recontar() restaura el estado de victoria",
                tablero.nivelResuelto() && tablero.getCajasEnDestino() == 1);
    }

    // ── Verificación ─────────────────────────────────────────────────────────

    private static void check(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[OK]   " : "[FALLA] ") + descripcion);
        if (!condicion) fallos++;
    }
}
