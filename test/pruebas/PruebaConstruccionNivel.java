package pruebas;

import modelo.Canal;
import modelo.GestorDeCanales;
import modelo.Tablero;
import utilidades.LectorDeNivel;

/**
 * Prueba de humo del camino completo de construccion: lector -> fabrica ->
 * parser -> constructor -> gestor de canales. Verifica que un nivel con
 * cerrojos y muros se carga y que el patron Observer queda cableado.
 */
public class PruebaConstruccionNivel {

    public static void main(String[] args) {
        String ruta = "niveles/nivel_cerrojos.txt";
        Tablero tablero = new LectorDeNivel().cargarNivel(ruta);

        if (tablero == null) {
            System.out.println("[FALLA] no se pudo cargar " + ruta);
            System.exit(1);
        }

        GestorDeCanales gestor = tablero.getGestorDeCanales();
        Canal azul = gestor.obtener("AZUL");

        boolean ok = azul != null && !azul.estaAbierto();
        System.out.println((ok ? "[OK]   " : "[FALLA] ")
                + "Construccion: canal AZUL registrado y muro cerrado al inicio");

        if (!ok) {
            System.exit(1);
        }
        System.out.println("PRUEBA DE CONSTRUCCION OK");
    }
}
