package vista;

import modelo.CalculadoraDePuntaje;

import javax.swing.*;
import java.awt.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;


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
    
    private final JButton btnReiniciar;
    private final JButton btnDeshacer;
    
    // Panel interno para los indicadores para no romper crearIndicador
    private final JPanel pnlIndicadores;

    // ─────────────────────────────────────────────────────────────────────────
    public PanelHUD(int totalDestinos, int pasosPorRetroceso, int nivelActual, int totalNiveles) {
        this.totalDestinos     = totalDestinos;
        this.pasosPorRetroceso = pasosPorRetroceso;
        this.nivelActual       = nivelActual;
        this.totalNiveles      = totalNiveles;

        setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        pnlIndicadores = new JPanel(new GridLayout(1, 7, 16, 0));
        pnlIndicadores.setOpaque(false);
        pnlIndicadores.setBorder(BorderFactory.createEmptyBorder(8, 16, 4, 16));

        lblTiempoValor  = crearIndicador("TIEMPO",      "00:00");
        lblMovValor     = crearIndicador("MOVIMIENTOS", "0");
        lblEmpujesValor = crearIndicador("EMPUJES",     "0");
        lblCajasValor   = crearIndicador("CAJAS",       "0 / " + totalDestinos);
        lblRetroValor   = crearIndicador("RETROCESOS",  "0 (0)");
        lblPuntajeValor = crearIndicador("PUNTAJE",     String.valueOf(puntaje));
        lblNivelValor   = crearIndicador("NIVEL",       nivelActual + " / " + totalNiveles);

        lblRetroValor.setForeground(COLOR_ALERTA);

        ImageIcon iconAzul = null;
        ImageIcon iconNaranja = null;
        try {
            String dir = System.getProperty("user.dir") + "/public/images/portal/";
            BufferedImage imgAzul = ImageIO.read(new File(dir + "azulFrente.png"));
            BufferedImage imgNaranja = ImageIO.read(new File(dir + "naranjaFrente.png"));
            iconAzul = new ImageIcon(imgAzul.getScaledInstance(24, 24, Image.SCALE_SMOOTH));
            iconNaranja = new ImageIcon(imgNaranja.getScaledInstance(24, 24, Image.SCALE_SMOOTH));
        } catch (Exception e) {}

        JLabel lblPortalAzul = new JLabel("Click Izq", iconAzul, SwingConstants.LEFT);
        lblPortalAzul.setForeground(new Color(100, 200, 255));
        lblPortalAzul.setFont(new Font("Monospaced", Font.BOLD, 14));

        JLabel lblPortalNaranja = new JLabel("Click Der", iconNaranja, SwingConstants.RIGHT);
        lblPortalNaranja.setForeground(new Color(255, 150, 50));
        lblPortalNaranja.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblPortalNaranja.setHorizontalTextPosition(SwingConstants.LEFT);

        JPanel pnlPortales = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 0));
        pnlPortales.setOpaque(false);
        pnlPortales.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        pnlPortales.add(lblPortalAzul);
        pnlPortales.add(lblPortalNaranja);

        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        pnlBotones.setOpaque(false);
        pnlBotones.setBorder(BorderFactory.createEmptyBorder(0, 16, 8, 16));
        
        btnDeshacer = new JButton("↶ Deshacer (Z)");
        estilizarBoton(btnDeshacer, new Color(80, 80, 100));
        
        btnReiniciar = new JButton("⟲ Reiniciar Nivel");
        estilizarBoton(btnReiniciar, new Color(200, 60, 60));
        
        pnlBotones.add(btnDeshacer);
        pnlBotones.add(btnReiniciar);

        JPanel pnlSur = new JPanel(new BorderLayout());
        pnlSur.setOpaque(false);
        pnlSur.add(pnlPortales, BorderLayout.NORTH);
        pnlSur.add(pnlBotones, BorderLayout.SOUTH);

        add(pnlIndicadores, BorderLayout.CENTER);
        add(pnlSur, BorderLayout.SOUTH);

        timerSwing = new Timer(1000, e -> {
            actualizarTiempoUI();
            actualizarPuntaje();
        });
    }

    public void setAccionReiniciar(Runnable accion) {
        btnReiniciar.addActionListener(e -> accion.run());
    }

    public void setAccionDeshacer(Runnable accion) {
        btnDeshacer.addActionListener(e -> accion.run());
    }

    private void estilizarBoton(JButton btn, Color bgColor) {
        btn.setFocusable(false);
        btn.setFont(new Font("Monospaced", Font.BOLD, 13));
        btn.setBackground(bgColor);
        btn.setForeground(Color.WHITE);
        btn.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(bgColor.darker(), 1),
            BorderFactory.createEmptyBorder(6, 12, 6, 12)
        ));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor.brighter());
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(bgColor);
            }
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

        pnlIndicadores.add(panel);
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
