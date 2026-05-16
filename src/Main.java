import controlador.ControladorJuego;
import vista.VentanaPrincipal;

public class Main {
    public static void main(String[] args) {
        // Inicializamos la vista y el controlador base
        VentanaPrincipal ventana = new VentanaPrincipal();
        ControladorJuego controlador = new ControladorJuego(ventana);

        // Arrancamos la aplicación
        controlador.iniciarJuego();
    }
}