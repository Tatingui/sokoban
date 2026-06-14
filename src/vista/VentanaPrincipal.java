package vista;

import modelo.Tablero;
import controlador.ControladorJuego;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.function.IntConsumer;

/**
 * Ventana principal del juego. Compone el PanelTablero (área de juego) y el
 * PanelHUD (información en tiempo real: movimientos, retrocesos, cajas).
 *
 * Como capa Vista del MVC, expone una API mínima al Controlador:
 *  - {@link #configurarControles(IntConsumer)}: delega el listener al PanelTablero.
 *  - {@link #actualizarVista()}: fuerza repaint del tablero.
 *  - {@link #actualizarHUD(int, int, int)}: sincroniza movimientos, retrocesos y cajas.
 *  - {@link #iniciarTiempo()}: arranca el timer del HUD.
 *  - {@link #solicitarFoco()}: da foco al panel de juego para capturar teclas.
 */
public class VentanaPrincipal extends JFrame {

    private final PanelTablero panelTablero;
    private final PanelHUD     panelHUD;

    public VentanaPrincipal(Tablero tablero) {
        setTitle("Sokoban - Javaneta  |  [Z] Deshacer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        int totalCajas = tablero.contarCajas();

        panelHUD     = new PanelHUD(totalCajas, ControladorJuego.LIMITE_HISTORIAL);
        panelTablero = new PanelTablero(tablero);

        add(panelHUD,     BorderLayout.NORTH);
        add(panelTablero, BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
    }

    // ── API para ControladorJuego ────────────────────────────────────────────

    /** Registra el consumidor de teclas en el PanelTablero. */
    public void configurarControles(IntConsumer alPresionarTecla) {
        panelTablero.configurarControles(alPresionarTecla);
    }

    /** Fuerza el repintado del área de juego. */
    public void actualizarVista() {
        panelTablero.repaint();
    }

    /**
     * Sincroniza el HUD con el estado actual de la partida.
     *
     * @param movimientos          movimientos realizados hasta el momento.
     * @param retrocesosDisponibles snapshots en la pila del Caretaker.
     * @param cajasEnDestino       cajas ya colocadas sobre casillas Destino.
     */
    public void actualizarHUD(int movimientos, int retrocesosDisponibles, int cajasEnDestino) {
        panelHUD.setMovimientos(movimientos);
        panelHUD.setRetrocesosDisponibles(retrocesosDisponibles);
        panelHUD.setCajasEnDestino(cajasEnDestino);
    }

    /** Arranca el timer del HUD (llamar al inicio de la partida). */
    public void iniciarTiempo() {
        panelHUD.iniciarTiempo();
    }

    /** Da el foco al panel de juego para que capture eventos de teclado. */
    public void solicitarFoco() {
        panelTablero.requestFocusInWindow();
    }
}
