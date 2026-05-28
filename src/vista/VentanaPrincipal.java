package vista;

import modelo.Tablero;
import javax.swing.JFrame;

public class VentanaPrincipal extends JFrame {

    private PanelTablero panelTablero;

    public VentanaPrincipal(Tablero tablero) {
        setTitle("Sokoban - Javaneta");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        panelTablero = new PanelTablero(tablero);
        add(panelTablero);

        pack(); // La ventana se ajusta al tamaño del panel
        setLocationRelativeTo(null); // Centra en pantalla
    }
}