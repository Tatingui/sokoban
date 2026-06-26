package vista;

import modelo.CalculadoraDePuntaje;

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
    private int empujes              = 0;
    private int cajasEnDestino       = 0;
    private int retrocesosDisponibles = 0;
    private int deshacerUsados       = 0;
    private int puntaje              = CalculadoraDePuntaje.calcular(0, 0, 0);
    private final int totalDestinos;
    private final int pasosPorRetroceso;
    private final int nivelActual;
    private final int totalNiveles;

    // ── Tiempo ───────────────────────────────────────────────────────────────
    private long tiempoInicioMs      = 0;
    private long tiempoAcumuladoMs   = 0;  // pausa sin perder el tiempo previo
    private boolean corriendo        = false;
    private final Timer timerSwing;         // actualiza el panel cada 1 segundo

    // ── Componentes de UI ────────────────────────────────────────────────────
    private final JLabel lblTiempoValor;
    private final JLabel lblMovValor;
    private final JLabel lblEmpujesValor;
    private final JLabel lblCajasValor;
    private final JLabel lblRetroValor;
    private final JLabel lblPuntajeValor;
    private final JLabel lblNivelValor;

    // ─────────────────────────────────────────────────────────────────────────
    public PanelHUD(int totalDestinos, int pasosPorRetroceso, int nivelActual, int totalNiveles) {
        this.totalDestinos     = totalDestinos;
        this.pasosPorRetroceso = pasosPorRetroceso;
        this.nivelActual       = nivelActual;
        this.totalNiveles      = totalNiveles;

        setBackground(COLOR_FONDO);
        setLayout(new GridLayout(1, 7, 16, 0));  // 7 indicadores
        setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));

        lblTiempoValor  = crearIndicador("TIEMPO",      "00:00");
        lblMovValor     = crearIndicador("MOVIMIENTOS", "0");
        lblEmpujesValor = crearIndicador("EMPUJES",     "0");
        lblCajasValor   = crearIndicador("CAJAS",       "0 / " + totalDestinos);
        lblRetroValor   = crearIndicador("RETROCESOS",  "0 (0)");
        lblPuntajeValor = crearIndicador("PUNTAJE",     String.valueOf(puntaje));
        lblNivelValor   = crearIndicador("NIVEL",       nivelActual + " / " + totalNiveles);

        lblRetroValor.setForeground(COLOR_ALERTA);  // arranca en rojo: aún no se puede retroceder

        // Timer de Swing: cada segundo refresca tiempo y puntaje (que depende del tiempo).
        timerSwing = new Timer(1000, e -> {
            actualizarTiempoUI();
            actualizarPuntaje();
        });
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
        empujes               = 0;
        cajasEnDestino        = 0;
        retrocesosDisponibles = 0;
        deshacerUsados        = 0;
        tiempoAcumuladoMs     = 0;
        actualizarUI();
    }

    public void setMovimientos(int n) {
        movimientos = n;
        lblMovValor.setText(String.valueOf(n));
        actualizarPuntaje();
    }

    public void setEmpujes(int n) {
        empujes = n;
        lblEmpujesValor.setText(String.valueOf(n));
    }

    public void setCajasEnDestino(int n) {
        cajasEnDestino = n;
        lblCajasValor.setText(n + " / " + totalDestinos);
        lblCajasValor.setForeground(n == totalDestinos ? COLOR_ACENTO : COLOR_TEXTO);
    }

    public void setRetrocesosDisponibles(int n) {
        retrocesosDisponibles = n;
        actualizarRetroLabel();
    }

    /** Usos del botón de deshacer; alimenta el cálculo de puntaje (no se muestra en el HUD). */
    public void setDeshacerUsados(int n) {
        deshacerUsados = n;
        actualizarPuntaje();
    }

    /** Puntaje actual, usado por la pantalla de victoria. */
    public int getPuntaje() {
        return CalculadoraDePuntaje.calcular((int) getSegundosTranscurridos(), movimientos, deshacerUsados);
    }

    // ── Métodos privados ─────────────────────────────────────────────────────

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

    private void actualizarPuntaje() {
        puntaje = getPuntaje();
        lblPuntajeValor.setText(String.valueOf(puntaje));
    }

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

    /** Segundos transcurridos de partida (acumulado + tramo actual si corre). */
    private long getSegundosTranscurridos() {
        long totalMs = tiempoAcumuladoMs;
        if (corriendo) totalMs += System.currentTimeMillis() - tiempoInicioMs;
        return totalMs / 1000;
    }

    /** Recalcula el tiempo transcurrido y lo muestra en formato MM:SS. */
    private void actualizarTiempoUI() {
        long totalSeg = getSegundosTranscurridos();
        long minutos  = totalSeg / 60;
        long segundos = totalSeg % 60;
        lblTiempoValor.setText(String.format("%02d:%02d", minutos, segundos));
    }

    /** Refresca todos los valores en pantalla (útil al reiniciar). */
    private void actualizarUI() {
        actualizarTiempoUI();
        lblMovValor.setText(String.valueOf(movimientos));
        lblEmpujesValor.setText(String.valueOf(empujes));
        lblCajasValor.setText(cajasEnDestino + " / " + totalDestinos);
        actualizarRetroLabel();
        actualizarPuntaje();
        lblCajasValor.setForeground(COLOR_TEXTO);
    }
}
