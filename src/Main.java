// Main.java
import controlador.ControladorJuego;
import modelo.Tablero;
import utilidades.LectorDeNivel;
import vista.VentanaPrincipal;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Resuelve la ruta relativa desde donde se ejecuta el proceso
        String ruta = System.getProperty("user.dir") + "/niveles/nivel1.txt";
        System.out.println("Buscando nivel en: " + ruta); // Para debug

        LectorDeNivel lector = new LectorDeNivel();
        Tablero tablero = lector.cargarNivel(ruta);

        if (tablero == null) {
            System.out.println("Error al cargar el nivel.");
            return;
        }

        SwingUtilities.invokeLater(() -> {
            VentanaPrincipal ventana = new VentanaPrincipal(tablero);
            ControladorJuego controlador = new ControladorJuego(ventana, tablero);
            controlador.iniciarJuego();
        });
    }
}
