// Main.java
import controlador.GestorDeNiveles;
import controlador.SecuenciaDeNiveles;

import javax.swing.SwingUtilities;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String base = System.getProperty("user.dir") + "/niveles/";
        SecuenciaDeNiveles secuencia = new SecuenciaDeNiveles(List.of(
                base + "nivel1.txt",
                base + "nivel2.txt",
                base + "nivel3.txt"));

        SwingUtilities.invokeLater(() -> new GestorDeNiveles(secuencia).iniciar());
    }
}
