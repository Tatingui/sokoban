package controlador;

import modelo.Tablero;
import utilidades.LectorDeNivel;
import vista.VentanaPrincipal;
import vista.DialogoVictoria;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Coordina el ciclo de vida de los niveles: carga el nivel actual, crea su
 * ventana y controlador, y al ganar muestra la pantalla de nivel completado
 * decidiendo si avanzar, reiniciar o terminar el juego.
 *
 * Usa {@link SecuenciaDeNiveles} para la lógica de orden de niveles y deja al
 * {@link ControladorJuego} el gameplay de cada nivel.
 */
public class GestorDeNiveles {

    private final SecuenciaDeNiveles secuencia;
    private final LectorDeNivel lector = new LectorDeNivel();
    private VentanaPrincipal ventana;

    public GestorDeNiveles(SecuenciaDeNiveles secuencia) {
        this.secuencia = secuencia;
    }

    public void iniciar() {
        cargarNivelActual();
    }

    // ── Carga del nivel ───────────────────────────────────────────────────────

    private void cargarNivelActual() {
        Tablero tablero = lector.cargarNivel(secuencia.rutaActual());
        if (tablero == null) {
            JOptionPane.showMessageDialog(ventana,
                    "No se pudo cargar el nivel: " + secuencia.rutaActual(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (ventana != null) ventana.dispose();
        ventana = new VentanaPrincipal(tablero, secuencia.numeroActual(), secuencia.total());
        ventana.setTitle("Sokoban - Nivel " + secuencia.numeroActual() + "/" + secuencia.total());

        ControladorJuego controlador = new ControladorJuego(ventana, tablero, this::alGanarNivel, this::cargarNivelActual);
        controlador.iniciarJuego();
    }

    // ── Pantalla de nivel completado ──────────────────────────────────────────

    private void alGanarNivel(EstadisticasNivel stats) {
        if (secuencia.haySiguiente()) {
            String titulo = "¡Nivel " + secuencia.numeroActual() + " completado!";
            DialogoVictoria dialog = new DialogoVictoria(ventana, titulo, stats, true, 
                () -> { secuencia.avanzar(); SwingUtilities.invokeLater(this::cargarNivelActual); },
                () -> { SwingUtilities.invokeLater(this::cargarNivelActual); }
            );
            dialog.setVisible(true);
        } else {
            String titulo = "¡Juego Completado!";
            DialogoVictoria dialog = new DialogoVictoria(ventana, titulo, stats, false, 
                null,
                () -> { secuencia.reiniciar(); SwingUtilities.invokeLater(this::cargarNivelActual); }
            );
            dialog.setVisible(true);
        }
    }
}
