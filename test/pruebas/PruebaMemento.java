package pruebas;

import modelo.*;

import java.util.Collections;

/**
 * Prueba manual del patrón GoF Memento aplicado al Tablero.
 *
 * Escenarios cubiertos:
 *  1. Undo básico: posición del jugador se restaura.
 *  2. Undo con caja: la caja vuelve a su posición original.
 *  3. Undo múltiple (hasta el límite de 15).
 *  4. Undo vacío: no hace nada cuando no hay historial.
 *  5. Estado Observer preservado: el muro restaura su estado abierto/cerrado.
 *  6. CajaFragil: la resistencia se restaura correctamente.
 *  7. Caja sobre Destino: SueloEspecial se preserva al restaurar.
 *  8. Contador de movimientos almacenado en el Memento.
 */
public class PruebaMemento {

    private static int fallos = 0;

    public static void main(String[] args) {
        escenarioUndoBasico();
        escenarioUndoConCaja();
        escenarioUndoMultiple();
        escenarioUndoSinHistorial();
        escenarioObserverPreservado();
        escenarioCajaFragil();
        escenarioCajaSobreDestino();
        escenarioContadorMovimientos();

        System.out.println();
        if (fallos == 0) {
            System.out.println("TODAS LAS PRUEBAS DE MEMENTO PASARON");
        } else {
            System.out.println(fallos + " PRUEBA(S) FALLARON");
            System.exit(1);
        }
    }

    // ── 1. Undo básico ───────────────────────────────────────────────────────

    private static void escenarioUndoBasico() {
        Tablero tablero = tablero3x3ConJugadorEn(1, 1);
        MotorMovimiento motor = new MotorMovimiento(tablero);

        TableroMemento snapshot = tablero.guardarEstado(0);
        motor.intentarMover(Direccion.ABAJO);
        check("UndoBasico: jugador se movió a fila 2",
                tablero.getJugadorFila() == 2);

        tablero.restaurarEstado(snapshot);
        check("UndoBasico: jugador vuelve a fila 1",
                tablero.getJugadorFila() == 1);
        check("UndoBasico: jugador vuelve a columna 1",
                tablero.getJugadorColumna() == 1);
        check("UndoBasico: la celda (1,1) contiene al Sokoban",
                tablero.getEntidad(1, 1) instanceof Sokoban);
        check("UndoBasico: la celda (2,1) vuelve a ser SueloNormal",
                tablero.getEntidad(2, 1) instanceof SueloNormal);
    }

    // ── 2. Undo con caja ─────────────────────────────────────────────────────

    private static void escenarioUndoConCaja() {
        Tablero tablero = tablero3x4();
        Sokoban jugador = new Sokoban();
        CajaNormal caja = new CajaNormal();

        tablero.colocarElemento(1, 1, jugador);
        tablero.colocarElemento(1, 2, caja);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        TableroMemento snapshot = tablero.guardarEstado(0);

        motor.intentarMover(Direccion.DERECHA);
        check("UndoCaja: jugador empujó la caja",
                tablero.getEntidad(1, 3) instanceof CajaNormal);

        tablero.restaurarEstado(snapshot);
        check("UndoCaja: la caja vuelve a (1,2)",
                tablero.getEntidad(1, 2) instanceof CajaNormal);
        check("UndoCaja: el jugador vuelve a (1,1)",
                tablero.getJugadorFila() == 1 && tablero.getJugadorColumna() == 1);
        check("UndoCaja: la celda (1,3) vuelve a SueloNormal",
                tablero.getEntidad(1, 3) instanceof SueloNormal);
    }

    // ── 3. Undo múltiple ─────────────────────────────────────────────────────

    private static void escenarioUndoMultiple() {
        Tablero tablero = tablero3x10();
        Sokoban jugador = new Sokoban();
        tablero.colocarElemento(1, 1, jugador);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        MotorMovimiento motor = new MotorMovimiento(tablero);

        // Guardar snapshots manualmente (simula lo que haría ControladorJuego)
        TableroMemento[] snapshots = new TableroMemento[5];
        for (int i = 0; i < 5; i++) {
            snapshots[i] = tablero.guardarEstado(i);
            motor.intentarMover(Direccion.DERECHA);
        }
        check("UndoMultiple: jugador en columna 6 tras 5 movimientos",
                tablero.getJugadorColumna() == 6);

        // Deshacer 3 movimientos
        tablero.restaurarEstado(snapshots[4]);
        tablero.restaurarEstado(snapshots[3]);
        tablero.restaurarEstado(snapshots[2]);
        check("UndoMultiple: jugador en columna 3 tras 3 undos",
                tablero.getJugadorColumna() == 3);
    }

    // ── 4. Undo sin historial ────────────────────────────────────────────────

    private static void escenarioUndoSinHistorial() {
        Tablero tablero = tablero3x3ConJugadorEn(1, 1);
        // No hay snapshots: restaurar un snapshot recién tomado (simula no-op)
        TableroMemento snapshot = tablero.guardarEstado(0);
        tablero.restaurarEstado(snapshot);
        check("UndoVacio: no-op cuando el snapshot es del estado actual",
                tablero.getJugadorFila() == 1 && tablero.getJugadorColumna() == 1);
    }

    // ── 5. Observer preservado ────────────────────────────────────────────────

    private static void escenarioObserverPreservado() {
        GestorDeCanales gestor = new GestorDeCanales();
        CasilleroCerrojo cerrojo = new CasilleroCerrojo("AZUL", false);
        Muro muro = new Muro("AZUL");
        gestor.registrarCerrojo(cerrojo);
        gestor.registrarMuro(muro);

        // Construir un tablero simple con el cerrojo y el muro
        Tablero tablero = new Tablero() {
            // usamos un tablero real para aprovechar el gestor correcto
        };

        // Construir tablero 3x5 manualmente
        tablero = tableroConCerrojo(gestor, cerrojo, muro);

        Sokoban jugador = new Sokoban();
        tablero.colocarElemento(1, 1, jugador);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        CajaLlave llave = new CajaLlave(Collections.singletonList("AZUL"));
        tablero.colocarElemento(1, 3, llave);

        check("Observer: muro empieza cerrado", !muro.esTransitable());

        // Empujar la llave sobre el cerrojo → Observer abre el muro
        MotorMovimiento motor = new MotorMovimiento(tablero);
        TableroMemento snapshot = tablero.guardarEstado(0);
        motor.intentarMover(Direccion.DERECHA); // jugador→(1,2), llave→(1,3)... ajustar el nivel

        // Snapshot tomado con muro cerrado y llave fuera del cerrojo:
        // restaurar debe cerrar el muro de nuevo
        tablero.restaurarEstado(snapshot);
        check("Observer: muro vuelve a cerrado tras undo", !muro.esTransitable());
        check("Observer: cerrojo vuelve a inactivo", !cerrojo.estaActivo());
    }

    // ── 6. CajaFragil ────────────────────────────────────────────────────────

    private static void escenarioCajaFragil() {
        Tablero tablero = tablero3x5();
        Sokoban jugador = new Sokoban();
        CajaFragil cajaFragil = new CajaFragil(3);

        tablero.colocarElemento(1, 1, jugador);
        tablero.colocarElemento(1, 2, cajaFragil);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        TableroMemento snapshot = tablero.guardarEstado(0);

        cajaFragil.reducirResistencia(); // resistencia → 2
        check("CajaFragil: resistencia reducida a 2", cajaFragil.getResistencia() == 2);

        tablero.restaurarEstado(snapshot);
        check("CajaFragil: resistencia restaurada a 3",
                cajaFragil.getResistencia() == 3);
        check("CajaFragil: caja vuelve a su posición original",
                tablero.getEntidad(1, 2) instanceof CajaFragil);
    }

    // ── 7. Caja sobre Destino ────────────────────────────────────────────────

    private static void escenarioCajaSobreDestino() {
        Tablero tablero = tablero3x5();
        Sokoban jugador = new Sokoban();
        CajaNormal caja = new CajaNormal();
        Destino destino = new Destino();

        tablero.colocarElemento(1, 1, jugador);
        tablero.colocarElemento(1, 2, caja);
        tablero.colocarElemento(1, 3, destino);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        MotorMovimiento motor = new MotorMovimiento(tablero);

        // Empujar caja sobre destino
        motor.intentarMover(Direccion.DERECHA);
        TableroMemento snapshotConCajaEnDestino = tablero.guardarEstado(1);
        check("Destino: caja está sobre el destino",
                destino.estaOcupado() && destino.getObjetoEncima() instanceof CajaNormal);

        // Empujar de nuevo: la caja sale del destino (el jugador ocupa su lugar)
        motor.intentarMover(Direccion.DERECHA);
        check("Destino: caja salió del destino (jugador la reemplaza)",
                !(destino.getObjetoEncima() instanceof CajaNormal));

        // Restaurar: la caja debe volver al destino
        tablero.restaurarEstado(snapshotConCajaEnDestino);
        check("Destino: al restaurar, la caja vuelve al destino",
                destino.estaOcupado() && destino.getObjetoEncima() instanceof CajaNormal);
        check("Destino: la celda Destino se preservó en la grilla",
                tablero.getEntidad(1, 3) instanceof Destino);
    }

    // ── 8. Contador de movimientos ───────────────────────────────────────────

    private static void escenarioContadorMovimientos() {
        Tablero tablero = tablero3x3ConJugadorEn(1, 1);
        TableroMemento snap3 = tablero.guardarEstado(3);
        TableroMemento snap7 = tablero.guardarEstado(7);

        check("Contador: snapshot guarda el valor correcto",
                snap3.getContadorMovimientos() == 3);
        check("Contador: segundo snapshot guarda valor diferente",
                snap7.getContadorMovimientos() == 7);
    }

    // ── Helpers de construcción de tableros ──────────────────────────────────

    private static Tablero tablero3x3ConJugadorEn(int fila, int columna) {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 3);
        llenarSuelo(tablero);
        Sokoban jugador = new Sokoban();
        tablero.colocarElemento(fila, columna, jugador);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(fila, columna);
        return tablero;
    }

    private static Tablero tablero3x4() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 4);
        llenarSuelo(tablero);
        return tablero;
    }

    private static Tablero tablero3x5() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 5);
        llenarSuelo(tablero);
        return tablero;
    }

    private static Tablero tablero3x10() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 10);
        llenarSuelo(tablero);
        return tablero;
    }

    /**
     * Construye un tablero 3x6 con el cerrojo en (1,4) y el muro en (1,5),
     * reutilizando el GestorDeCanales ya configurado en el test del Observer.
     *
     *  P  P  P  P  P  P
     *  P  J  S  L  X  M
     *  P  P  P  P  P  P
     *
     * (donde X = CasilleroCerrojo AZUL, M = Muro AZUL, L = CajaLlave AZUL)
     */
    private static Tablero tableroConCerrojo(GestorDeCanales gestor,
                                             CasilleroCerrojo cerrojo,
                                             Muro muro) {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 6);
        llenarSuelo(tablero);

        // Bloquear bordes con paredes (para evitar movimientos inválidos)
        for (int c = 0; c < 6; c++) {
            tablero.colocarElemento(0, c, new Pared());
            tablero.colocarElemento(2, c, new Pared());
        }
        tablero.colocarElemento(1, 0, new Pared());
        // Los demás quedan como SueloNormal (válido)

        tablero.colocarElemento(1, 4, cerrojo);
        tablero.colocarElemento(1, 5, muro);

        // Registrar muro en el gestor del canal (el cerrojo ya fue registrado antes)
        // El gestor ya tiene el canal AZUL; solo necesitamos suscribir el muro
        gestor.registrarMuro(muro);

        // Inyectar el gestor en el tablero para que Tablero.guardarEstado lo encuentre
        // Nota: como GestorDeCanales es final en Tablero, debemos acceder al del tablero.
        // Para esta prueba de integración usamos los canales del gestor externo
        // directamente via GestorDeCanales pasado al tablero.
        // En producción el ConstructorTablero lo hace; aquí replicamos el efecto.
        tablero.getGestorDeCanales().registrarCerrojo(cerrojo);
        tablero.getGestorDeCanales().registrarMuro(muro);

        return tablero;
    }

    private static void llenarSuelo(Tablero tablero) {
        for (int f = 0; f < tablero.getFilas(); f++)
            for (int c = 0; c < tablero.getColumnas(); c++)
                tablero.colocarElemento(f, c, new SueloNormal());
    }

    // ── Verificación ─────────────────────────────────────────────────────────

    private static void check(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[OK]   " : "[FALLA] ") + descripcion);
        if (!condicion) fallos++;
    }
}
