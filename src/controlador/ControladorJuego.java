package controlador;

import modelo.ColorPortal;
import modelo.Direccion;
import modelo.MotorMovimiento;
import modelo.ResultadoMovimiento;
import modelo.TableroMemento;
import modelo.Tablero;
import sonido.GestorDeSonido;
import vista.VentanaPrincipal;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * ControladorJuego — coordinador MVC. El Caretaker del patrón GoF Memento es
 * {@link HistorialDeMovimientos}, al que este controlador delega por completo
 * el almacenamiento y descarte de snapshots.
 *
 * Responsabilidades:
 *  - Captura el input de teclado y lo traduce a comandos de dominio.
 *  - Delega el movimiento a {@link MotorMovimiento}.
 *  - Antes de cada movimiento exitoso: solicita un snapshot al Originator
 *    (Tablero) y lo entrega al {@link HistorialDeMovimientos} para que lo apile.
 *  - Al recibir la tecla de undo: le pide al historial el snapshot al que
 *    retroceder y, si existe, ordena al Tablero que lo restaure.
 *  - Mantiene {@link #contadorMovimientos} y lo sincroniza con el HUD tras cada
 *    operación (movimiento o undo).
 *  - Ordena a la vista que se repinte cuando el modelo cambia.
 */
public class ControladorJuego {

    private final VentanaPrincipal vista;
    private final Tablero          tablero;
    private final MotorMovimiento  motorMovimiento;

    /** Se invoca al resolver el nivel, con las estadísticas de la partida. */
    private final Consumer<EstadisticasNivel> alGanarNivel;

    private final Runnable alReiniciar;

    /** Caretaker del Memento: guarda y descarta los snapshots del Tablero. */
    private final HistorialDeMovimientos historial = new HistorialDeMovimientos();

    /** Número de movimientos realizados en la partida actual. */
    private int contadorMovimientos = 0;

    /** Empujes de cajas efectuados (monótono; no se revierte con el undo). */
    private int contadorEmpujes = 0;

    /** Cantidad de veces que se usó el botón de deshacer (monótono). */
    private int contadorDeshacer = 0;

    // ─────────────────────────────────────────────────────────────────────────

    public ControladorJuego(VentanaPrincipal vista, Tablero tablero,
                            Consumer<EstadisticasNivel> alGanarNivel, Runnable alReiniciar) {
        this.vista           = vista;
        this.tablero         = tablero;
        this.alGanarNivel    = alGanarNivel;
        this.alReiniciar     = alReiniciar;
        this.motorMovimiento = new MotorMovimiento(tablero);
        vista.configurarControles(this::procesarTecla);
        vista.configurarPortales(
                () -> dispararPortal(ColorPortal.AZUL),
                () -> dispararPortal(ColorPortal.NARANJA));
        
        vista.configurarBotonesUI(
            () -> { deshacerMovimiento(); vista.solicitarFoco(); },
            () -> { alReiniciar.run(); vista.solicitarFoco(); }
        );
    }

    // ── API pública ──────────────────────────────────────────────────────────

    public void iniciarJuego() {
        sincronizarHUD();
        vista.iniciarTiempo();
        GestorDeSonido.getInstancia().reproducirMusicaFondo();
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
        if (codigoTecla == KeyEvent.VK_R) {
            alReiniciar.run();
            vista.solicitarFoco();
            return;
        }

        Direccion direccion = mapearDireccion(codigoTecla);
        if (direccion == null) return;

        // Estado de colocaciones correctas ANTES de mover (para el sonido).
        int cajasAntes    = tablero.getCajasEnDestino();
        int cerrojosAntes = tablero.contarCerrojosActivos();

        // Guardar snapshot ANTES de mover (se descarta si no hay movimiento real)
        TableroMemento snapshot = tablero.guardarEstado(contadorMovimientos);

        ResultadoMovimiento resultado = motorMovimiento.intentarMover(direccion);
        if (resultado.esAvance()) {
            contadorMovimientos++;
            GestorDeSonido sonido = GestorDeSonido.getInstancia();
            if (resultado == ResultadoMovimiento.EMPUJE) {
                contadorEmpujes++;
                sonido.moverCaja();
            } else {
                sonido.caminar();
            }
            // Caja válida que quedó en destino, o llave que activó un cerrojo.
            if (tablero.getCajasEnDestino() > cajasAntes
                    || tablero.contarCerrojosActivos() > cerrojosAntes) {
                sonido.cajaEnDestino();
            }

            historial.apilar(snapshot);
            sincronizarHUD();
            vista.actualizarVista();
            if (tablero.nivelResuelto()) {
                manejarVictoria();
            }
        } else if (resultado == ResultadoMovimiento.GIRO) {
            // Girar solo cambia la mirada: repintamos el sprite, sin snapshot ni conteo.
            vista.actualizarVista();
        }
        // SIN_CAMBIO (pared, caja inamovible, borde): nada cambió.
    }

    /**
     * Reacciona a la condición de victoria detectada por el Observer
     * ({@link modelo.GestorDeVictoria}) tras un movimiento, delegando en el
     * coordinador de niveles (que muestra la pantalla y decide el flujo).
     */
    /**
     * Dispara (o reposiciona) un portal en la dirección que mira Sokoban. No es
     * un movimiento: no cuenta ni genera snapshot, solo reubica el portal y repinta.
     */
    private void dispararPortal(ColorPortal color) {
        boolean colocado = tablero.getGestorDePortales().disparar(tablero, color,
                tablero.getJugadorFila(), tablero.getJugadorColumna(),
                tablero.getJugador().getMirada());
        if (colocado) {
            GestorDeSonido.getInstancia().abrirPortal();
            vista.actualizarVista();
        }
    }

    private void manejarVictoria() {
        vista.detenerTiempo();
        GestorDeSonido.getInstancia().victoria();
        EstadisticasNivel stats = new EstadisticasNivel(
                contadorMovimientos, contadorEmpujes, contadorDeshacer, vista.getPuntaje());
        alGanarNivel.accept(stats);
    }

    // ── Undo ──────────────────────────────────────────────────────────────────

    /**
     * Le pide al {@link HistorialDeMovimientos} el snapshot al que retroceder
     * (5 pasos de una sola vez) y, si existe, ordena al Tablero que lo restaure.
     * No hace nada si el historial no tiene suficientes snapshots aún.
     */
    private void deshacerMovimiento() {
        TableroMemento objetivo = historial.retroceder();
        if (objetivo == null) return;

        tablero.restaurarEstado(objetivo);
        contadorMovimientos = objetivo.getContadorMovimientos();
        contadorDeshacer++;   // monótono: penaliza el puntaje aunque se rehaga
        sincronizarHUD();
        vista.actualizarVista();
    }

    // ── Helpers privados ─────────────────────────────────────────────────────

    /** Propaga el estado de los contadores y el historial al HUD de la vista. */
    private void sincronizarHUD() {
        int retrocesosDisponibles = historial.size();
        int cajasEnDestino        = tablero.getCajasEnDestino();
        vista.actualizarHUD(contadorMovimientos, contadorEmpujes, retrocesosDisponibles,
                            contadorDeshacer, cajasEnDestino);
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
