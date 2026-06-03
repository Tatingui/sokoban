package pruebas;

import modelo.Canal;
import modelo.Destino;
import modelo.Entidad;
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

        boolean canalOk = azul != null && !azul.estaAbierto();
        System.out.println((canalOk ? "[OK]   " : "[FALLA] ")
                + "Construccion: canal AZUL registrado y muro cerrado al inicio");

        // El token 'D' debe instanciarse como Destino (antes caia a SueloNormal).
        boolean destinoOk = contieneDestino(tablero);
        System.out.println((destinoOk ? "[OK]   " : "[FALLA] ")
                + "Construccion: el token 'D' se instancia como Destino");

        if (!canalOk || !destinoOk) {
            System.exit(1);
        }
        System.out.println("PRUEBA DE CONSTRUCCION OK");
    }

    private static boolean contieneDestino(Tablero tablero) {
        for (Entidad[] filaEntidades : tablero.getGrilla()) {
            for (Entidad entidad : filaEntidades) {
                if (entidad instanceof Destino) {
                    return true;
                }
            }
        }
        return false;
    }
}
