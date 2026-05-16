package controlador;

import vista.VentanaPrincipal;

public class ControladorJuego {
    private VentanaPrincipal vista;

    public ControladorJuego(VentanaPrincipal vista) {
        this.vista = vista;
    }

    public void iniciarJuego() {
        this.vista.setVisible(true);
    }
}