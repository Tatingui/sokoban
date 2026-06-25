// MainSandbox.java
import controlador.ControladorJuego;
import modelo.Tablero;
import utilidades.LectorDeNivel;
import vista.VentanaPrincipal;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Punto de entrada alternativo para probar TODOS los elementos del juego en un
 * único nivel sandbox, sin tener que pasar por los 3 niveles reales.
 * Carga {@code niveles/sandbox.txt} y no encadena niveles: al ganar solo avisa.
 */
public class MainSandbox {
    public static void main(String[] args) {
        String ruta = System.getProperty("user.dir") + "/niveles/sandbox.txt";

        SwingUtilities.invokeLater(() -> {
            Tablero tablero = new LectorDeNivel().cargarNivel(ruta);
            if (tablero == null) {
                System.out.println("No se pudo cargar el sandbox: " + ruta);
                return;
            }
            VentanaPrincipal ventana = new VentanaPrincipal(tablero);
            ventana.setTitle("Sokoban - SANDBOX   |   [Z] Deshacer");
            ControladorJuego controlador = new ControladorJuego(ventana, tablero,
                    movimientos -> JOptionPane.showMessageDialog(ventana,
                            "¡Sandbox completado en " + movimientos + " movimientos!"));
            controlador.iniciarJuego();
        });
    }
}
