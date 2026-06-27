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
 *  - {@link #actualizarHUD(int, int, int, int, int)}: sincroniza el estado del HUD.
 *  - {@link #iniciarTiempo()}: arranca el timer del HUD.
 *  - {@link #solicitarFoco()}: da foco al panel de juego para capturar teclas.
 */
public class VentanaPrincipal extends JFrame {

    private final PanelTablero panelTablero;
    private final PanelHUD     panelHUD;

    public VentanaPrincipal(Tablero tablero, int nivelActual, int totalNiveles) {
        setTitle("Sokoban - Javaneta  |  [Z] Deshacer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());

        int totalDestinos = tablero.getGestorDeVictoria().getTotalDestinos();

        panelHUD     = new PanelHUD(totalDestinos, ControladorJuego.PASOS_POR_RETROCESO,
                                    nivelActual, totalNiveles);
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

    /** Registra los disparos de portal (click izquierdo / derecho) en el PanelTablero. */
    public void configurarPortales(Runnable alClickIzquierdo, Runnable alClickDerecho) {
        panelTablero.configurarPortales(alClickIzquierdo, alClickDerecho);
    }

    /** Conecta los botones de interfaz del HUD */
    public void configurarBotonesUI(Runnable alDeshacer, Runnable alReiniciar) {
        panelHUD.setAccionDeshacer(alDeshacer);
        panelHUD.setAccionReiniciar(alReiniciar);
    }

    /** Fuerza el repintado del área de juego. */
    public void actualizarVista() {
        panelTablero.repaint();
    }

    /**
     * Sincroniza el HUD con el estado actual de la partida.
     *
     * @param movimientos          movimientos realizados hasta el momento.
     * @param empujes              empujes de cajas efectuados.
     * @param retrocesosDisponibles snapshots en la pila del Caretaker.
     * @param deshacerUsados       cantidad de veces que se usó el botón de deshacer.
     * @param cajasEnDestino       cajas ya colocadas sobre casillas Destino.
     */
    public void actualizarHUD(int movimientos, int empujes, int retrocesosDisponibles,
                              int deshacerUsados, int cajasEnDestino) {
        panelHUD.setMovimientos(movimientos);
        panelHUD.setEmpujes(empujes);
        panelHUD.setRetrocesosDisponibles(retrocesosDisponibles);
        panelHUD.setDeshacerUsados(deshacerUsados);
        panelHUD.setCajasEnDestino(cajasEnDestino);
    }

    /** Puntaje actual calculado por el HUD (para la pantalla de victoria). */
    public int getPuntaje() {
        return panelHUD.getPuntaje();
    }

    /** Arranca el timer del HUD (llamar al inicio de la partida). */
    public void iniciarTiempo() {
        panelHUD.iniciarTiempo();
    }

    /** Detiene el timer del HUD (al ganar o pausar). */
    public void detenerTiempo() {
        panelHUD.detenerTiempo();
    }

    /** Da el foco al panel de juego para que capture eventos de teclado. */
    public void solicitarFoco() {
        panelTablero.requestFocusInWindow();
    }
}
