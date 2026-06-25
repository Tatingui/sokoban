package controlador;

import modelo.Direccion;
import modelo.MotorMovimiento;
import modelo.ResultadoMovimiento;
import modelo.TableroMemento;
import modelo.Tablero;
import vista.VentanaPrincipal;

import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.function.IntConsumer;

/**
 * ControladorJuego — Caretaker del patrón GoF Memento y coordinador MVC.
 *
 * Responsabilidades como Caretaker:
 *  - Mantiene el historial de snapshots (máximo {@link #LIMITE_HISTORIAL} movimientos).
 *  - Antes de cada movimiento exitoso: solicita un snapshot al Originator (Tablero)
 *    y lo apila.
 *  - Al recibir la tecla de undo: retrocede {@link #PASOS_POR_RETROCESO} pasos de
 *    una sola vez y pide al Tablero que restaure ese estado. Si hay menos de 5
 *    snapshots no hace nada (no hay retroceso parcial). Con tope 15 y salto de 5,
 *    se pueden hacer como máximo 3 undos seguidos.
 *  - Mantiene {@link #contadorMovimientos} y lo sincroniza con el HUD tras cada
 *    operación (movimiento o undo).
 *
 * Responsabilidades MVC:
 *  - Captura el input de teclado y lo traduce a comandos de dominio.
 *  - Delega el movimiento a {@link MotorMovimiento}.
 *  - Ordena a la vista que se repinte cuando el modelo cambia.
 */
public class ControladorJuego {

    /** Máximo de movimientos que se pueden deshacer (snapshots guardados). */
    public static final int LIMITE_HISTORIAL = 15;

    /** Pasos que retrocede cada pulsación del botón de undo. */
    public static final int PASOS_POR_RETROCESO = 5;

    private final VentanaPrincipal vista;
    private final Tablero          tablero;
    private final MotorMovimiento  motorMovimiento;

    /** Se invoca al resolver el nivel, con la cantidad de movimientos realizados. */
    private final IntConsumer alGanarNivel;

    /** Pila de snapshots: el tope es el estado ANTES del último movimiento. */
    private final Deque<TableroMemento> historial = new ArrayDeque<>();

    /** Número de movimientos realizados en la partida actual. */
    private int contadorMovimientos = 0;

    // ─────────────────────────────────────────────────────────────────────────

    public ControladorJuego(VentanaPrincipal vista, Tablero tablero, IntConsumer alGanarNivel) {
        this.vista           = vista;
        this.tablero         = tablero;
        this.alGanarNivel    = alGanarNivel;
        this.motorMovimiento = new MotorMovimiento(tablero);
        vista.configurarControles(this::procesarTecla);
    }

    // ── API pública ──────────────────────────────────────────────────────────

    public void iniciarJuego() {
        sincronizarHUD();
        vista.iniciarTiempo();
        vista.setVisible(true);
        vista.solicitarFoco();
    }

    public Tablero getTablero() { return tablero; }

    // ── Procesamiento de teclas ───────────────────────────────────────────────

    private void procesarTecla(int codigoTecla) {
        if (codigoTecla == KeyEvent.VK_Z) {
            deshacerMovimiento();
            return;
        }

        Direccion direccion = mapearDireccion(codigoTecla);
        if (direccion == null) return;

        // Guardar snapshot ANTES de mover (se descarta si no hay movimiento real)
        TableroMemento snapshot = tablero.guardarEstado(contadorMovimientos);

        switch (motorMovimiento.intentarMover(direccion)) {
            case MOVIMIENTO -> {
                contadorMovimientos++;
                apilarSnapshot(snapshot);
                sincronizarHUD();
                vista.actualizarVista();
                if (tablero.nivelResuelto()) {
                    manejarVictoria();
                }
            }
            // Girar solo cambia la mirada: repintamos el sprite, sin snapshot ni conteo.
            case GIRO -> vista.actualizarVista();
            // Bloqueado (pared, caja inamovible, borde): nada cambió.
            case SIN_CAMBIO -> { }
        }
    }

    /**
     * Reacciona a la condición de victoria detectada por el Observer
     * ({@link modelo.GestorDeVictoria}) tras un movimiento, delegando en el
     * coordinador de niveles (que muestra la pantalla y decide el flujo).
     */
    private void manejarVictoria() {
        vista.detenerTiempo();
        alGanarNivel.accept(contadorMovimientos);
    }

    // ── Caretaker: undo ───────────────────────────────────────────────────────

    /**
     * Retrocede exactamente {@link #PASOS_POR_RETROCESO} pasos de una sola vez:
     * desapila 5 snapshots y restaura el tablero al más antiguo de ellos. No hace
     * nada si quedan menos de 5 snapshots en el historial (no hay retroceso parcial).
     */
    private void deshacerMovimiento() {
        if (historial.size() < PASOS_POR_RETROCESO) return;

        TableroMemento objetivo = null;
        for (int i = 0; i < PASOS_POR_RETROCESO; i++) {
            objetivo = historial.pop();
        }

        tablero.restaurarEstado(objetivo);
        contadorMovimientos = objetivo.getContadorMovimientos();
        sincronizarHUD();
        vista.actualizarVista();
    }

    // ── Helpers privados ─────────────────────────────────────────────────────

    /**
     * Apila un snapshot, respetando el límite de historial.
     * Si se supera el límite se elimina el snapshot más antiguo (FIFO del fondo).
     */
    private void apilarSnapshot(TableroMemento snapshot) {
        if (historial.size() >= LIMITE_HISTORIAL) {
            historial.pollLast();  // descarta el más antiguo
        }
        historial.push(snapshot);  // apila al frente (más reciente)
    }

    /** Propaga el estado del contador y el historial al HUD de la vista. */
    private void sincronizarHUD() {
        int retrocesosDisponibles = historial.size();
        int cajasEnDestino        = tablero.getCajasEnDestino();
        vista.actualizarHUD(contadorMovimientos, retrocesosDisponibles, cajasEnDestino);
    }

    private Direccion mapearDireccion(int codigoTecla) {
        return switch (codigoTecla) {
            case KeyEvent.VK_UP,    KeyEvent.VK_W -> Direccion.ARRIBA;
            case KeyEvent.VK_DOWN,  KeyEvent.VK_S -> Direccion.ABAJO;
            case KeyEvent.VK_LEFT,  KeyEvent.VK_A -> Direccion.IZQUIERDA;
            case KeyEvent.VK_RIGHT, KeyEvent.VK_D -> Direccion.DERECHA;
            default -> null;
        };
    }
}
