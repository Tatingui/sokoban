package controlador;

import modelo.Direccion;
import modelo.MotorMovimiento;
import modelo.TableroMemento;
import modelo.Tablero;
import vista.VentanaPrincipal;

import java.awt.event.KeyEvent;
import java.util.ArrayDeque;
import java.util.Deque;

/**
 * ControladorJuego — Caretaker del patrón GoF Memento y coordinador MVC.
 *
 * Responsabilidades como Caretaker:
 *  - Mantiene el historial de snapshots (máximo {@link #LIMITE_HISTORIAL} movimientos).
 *  - Antes de cada movimiento exitoso: solicita un snapshot al Originator (Tablero)
 *    y lo apila.
 *  - Al recibir la tecla de undo: desapila el último snapshot y pide al Tablero
 *    que restaure el estado.
 *  - Mantiene {@link #contadorMovimientos} y lo sincroniza con el HUD tras cada
 *    operación (movimiento o undo).
 *
 * Responsabilidades MVC:
 *  - Captura el input de teclado y lo traduce a comandos de dominio.
 *  - Delega el movimiento a {@link MotorMovimiento}.
 *  - Ordena a la vista que se repinte cuando el modelo cambia.
 */
public class ControladorJuego {

    /** Máximo de movimientos que se pueden deshacer. */
    public static final int LIMITE_HISTORIAL = 15;

    private final VentanaPrincipal vista;
    private final Tablero          tablero;
    private final MotorMovimiento  motorMovimiento;

    /** Pila de snapshots: el tope es el estado ANTES del último movimiento. */
    private final Deque<TableroMemento> historial = new ArrayDeque<>();

    /** Número de movimientos realizados en la partida actual. */
    private int contadorMovimientos = 0;

    // ─────────────────────────────────────────────────────────────────────────

    public ControladorJuego(VentanaPrincipal vista, Tablero tablero) {
        this.vista           = vista;
        this.tablero         = tablero;
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

        // Guardar snapshot ANTES de mover (se descarta si el movimiento falla)
        TableroMemento snapshot = tablero.guardarEstado(contadorMovimientos);

        if (motorMovimiento.intentarMover(direccion)) {
            contadorMovimientos++;
            apilarSnapshot(snapshot);
            sincronizarHUD();
            vista.actualizarVista();
        }
    }

    // ── Caretaker: undo ───────────────────────────────────────────────────────

    /**
     * Deshace el último movimiento restaurando el tablero al estado del snapshot
     * en el tope de la pila. No hace nada si el historial está vacío.
     */
    private void deshacerMovimiento() {
        if (historial.isEmpty()) return;

        TableroMemento memento = historial.pop();
        tablero.restaurarEstado(memento);
        contadorMovimientos = memento.getContadorMovimientos();
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
        int cajasEnDestino        = tablero.contarCajasEnDestino();
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
