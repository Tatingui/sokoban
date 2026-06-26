package controlador;

import modelo.Tablero;
import utilidades.LectorDeNivel;
import vista.VentanaPrincipal;

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
        ventana.setTitle("Sokoban - Nivel " + secuencia.numeroActual() + "/" + secuencia.total()
                + "   |   [Z] Deshacer");

        ControladorJuego controlador = new ControladorJuego(ventana, tablero, this::alGanarNivel);
        controlador.iniciarJuego();
    }

    // ── Pantalla de nivel completado ──────────────────────────────────────────

    private void alGanarNivel(EstadisticasNivel stats) {
        String resumen = "Movimientos: " + stats.movimientos()
                + "\nEmpujes efectuados: " + stats.empujes()
                + "\nUsos de deshacer: " + stats.deshacerUsados()
                + "\nPuntaje obtenido: " + stats.puntaje();

        if (secuencia.haySiguiente()) {
            Object[] opciones = {"Siguiente nivel", "Reiniciar nivel"};
            int eleccion = JOptionPane.showOptionDialog(ventana,
                    "¡Nivel " + secuencia.numeroActual() + " de " + secuencia.total()
                            + " completado!\n\n" + resumen,
                    "Nivel completado",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, opciones, opciones[0]);

            if (eleccion == 0) secuencia.avanzar();   // si no, reinicia el nivel actual
            SwingUtilities.invokeLater(this::cargarNivelActual);

        } else {
            Object[] opciones = {"Jugar de nuevo", "Salir"};
            int eleccion = JOptionPane.showOptionDialog(ventana,
                    "¡Completaste los " + secuencia.total() + " niveles!\n\n" + resumen,
                    "¡Juego completado!",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE,
                    null, opciones, opciones[0]);

            if (eleccion == 0) {
                secuencia.reiniciar();
                SwingUtilities.invokeLater(this::cargarNivelActual);
            } else {
                System.exit(0);
            }
        }
    }
}
