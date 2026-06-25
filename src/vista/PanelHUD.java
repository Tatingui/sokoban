package vista;

import javax.swing.*;
import java.awt.*;


public class PanelHUD extends JPanel {

    // ── Colores del tema ─────────────────────────────────────────────────────
    private static final Color COLOR_FONDO      = new Color(30, 30, 40);
    private static final Color COLOR_TEXTO      = new Color(220, 220, 230);
    private static final Color COLOR_ACENTO     = new Color(255, 200, 60);   // amarillo cálido
    private static final Color COLOR_ALERTA     = new Color(220, 80, 80);    // rojo cuando undos bajos
    private static final Font  FUENTE_ETIQUETA  = new Font("Monospaced", Font.PLAIN, 11);
    private static final Font  FUENTE_VALOR     = new Font("Monospaced", Font.BOLD,  18);

    // ── Estado del HUD ───────────────────────────────────────────────────────
    private int movimientos          = 0;
    private int cajasEnDestino       = 0;
    private final int totalCajas;
    private int retrocesosDisponibles;
    private final int pasosPorRetroceso;

    // ── Tiempo ───────────────────────────────────────────────────────────────
    private long tiempoInicioMs      = 0;
    private long tiempoAcumuladoMs   = 0;  // pausa sin perder el tiempo previo
    private boolean corriendo        = false;
    private final Timer timerSwing;         // actualiza el panel cada 1 segundo

    // ── Componentes de UI ────────────────────────────────────────────────────
    private final JLabel lblTiempoValor;
    private final JLabel lblMovValor;
    private final JLabel lblCajasValor;
    private final JLabel lblRetroValor;

    // ─────────────────────────────────────────────────────────────────────────
    public PanelHUD(int totalDestinos, int pasosPorRetroceso) {
        this.totalCajas            = totalDestinos;
        this.pasosPorRetroceso     = pasosPorRetroceso;
        this.retrocesosDisponibles = 0;

        setBackground(COLOR_FONDO);
        setLayout(new GridLayout(1, 4, 16, 0));  // 4 columnas, separación horizontal
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        // Crear los 4 indicadores
        lblTiempoValor = crearIndicador("TIEMPO",     "00:00");
        lblMovValor    = crearIndicador("MOVIMIENTOS", "0");
        lblCajasValor  = crearIndicador("CAJAS",       "0 / " + totalDestinos);
        lblRetroValor  = crearIndicador("RETROCESOS",  "0 (0)");
        lblRetroValor.setForeground(COLOR_ALERTA);  // arranca en rojo: aún no se puede retroceder

        // Timer de Swing: dispara cada 1000 ms para refrescar el tiempo
        timerSwing = new Timer(1000, e -> actualizarTiempoUI());
    }

    // ── API pública ──────────────────────────────────────────────────────────

    /** Llama esto cuando empieza o se reanuda la partida. */
    public void iniciarTiempo() {
        tiempoInicioMs = System.currentTimeMillis();
        corriendo = true;
        timerSwing.start();
    }

    /** Llama esto al pausar, ganar o perder. */
    public void detenerTiempo() {
        if (corriendo) {
            tiempoAcumuladoMs += System.currentTimeMillis() - tiempoInicioMs;
            corriendo = false;
        }
        timerSwing.stop();
    }

    /** Resetea todo a cero (nueva partida). */
    public void reiniciar() {
        detenerTiempo();
        movimientos           = 0;
        cajasEnDestino        = 0;
        retrocesosDisponibles = 0;
        tiempoAcumuladoMs     = 0;
        actualizarUI();
    }

    public void setMovimientos(int n) {
        movimientos = n;
        lblMovValor.setText(String.valueOf(n));
    }

    public void setCajasEnDestino(int n) {
        cajasEnDestino = n;
        lblCajasValor.setText(n + " / " + totalCajas);
        // Resaltar cuando todas las cajas están en destino
        lblCajasValor.setForeground(n == totalCajas ? COLOR_ACENTO : COLOR_TEXTO);
    }

    public void setRetrocesosDisponibles(int n) {
        retrocesosDisponibles = n;
        actualizarRetroLabel();
    }

    /**
     * Muestra "snapshots (retrocesos posibles)", ej: 15 (3), 7 (1), 4 (0).
     * Queda en rojo mientras no se complete un bloque de {@code pasosPorRetroceso}
     * (es decir, mientras todavía no se pueda retroceder).
     */
    private void actualizarRetroLabel() {
        int posibles = retrocesosDisponibles / pasosPorRetroceso;
        lblRetroValor.setText(retrocesosDisponibles + " (" + posibles + ")");
        lblRetroValor.setForeground(posibles == 0 ? COLOR_ALERTA : COLOR_TEXTO);
    }

    // ── Métodos privados ─────────────────────────────────────────────────────

    /**
     * Crea un sub-panel con etiqueta descriptiva arriba y valor grande abajo.
     * Devuelve la JLabel del valor para poder actualizarla luego.
     */
    private JLabel crearIndicador(String etiqueta, String valorInicial) {
        JPanel panel = new JPanel(new BorderLayout(0, 2));
        panel.setOpaque(false);

        JLabel lblEtiqueta = new JLabel(etiqueta, SwingConstants.CENTER);
        lblEtiqueta.setFont(FUENTE_ETIQUETA);
        lblEtiqueta.setForeground(COLOR_ACENTO);

        JLabel lblValor = new JLabel(valorInicial, SwingConstants.CENTER);
        lblValor.setFont(FUENTE_VALOR);
        lblValor.setForeground(COLOR_TEXTO);

        panel.add(lblEtiqueta, BorderLayout.NORTH);
        panel.add(lblValor,    BorderLayout.CENTER);

        add(panel);
        return lblValor;  // referencia para actualizar después
    }

    /** Recalcula el tiempo transcurrido y lo muestra en formato MM:SS. */
    private void actualizarTiempoUI() {
        long totalMs = tiempoAcumuladoMs;
        if (corriendo) totalMs += System.currentTimeMillis() - tiempoInicioMs;

        long totalSeg = totalMs / 1000;
        long minutos  = totalSeg / 60;
        long segundos = totalSeg % 60;

        lblTiempoValor.setText(String.format("%02d:%02d", minutos, segundos));
    }

    /** Refresca todos los valores en pantalla (útil al reiniciar). */
    private void actualizarUI() {
        actualizarTiempoUI();
        lblMovValor.setText(String.valueOf(movimientos));
        lblCajasValor.setText(cajasEnDestino + " / " + totalCajas);
        actualizarRetroLabel();
        lblCajasValor.setForeground(COLOR_TEXTO);
    }
}
