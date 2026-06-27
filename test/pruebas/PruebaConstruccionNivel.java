package pruebas;

import modelo.Canal;
import modelo.Destino;
import modelo.Entidad;
import modelo.GestorDeCanales;
import modelo.Tablero;
import utilidades.ConstructorTablero;
import utilidades.DirectorDeTablero;

import java.util.List;

/**
 * Prueba del camino de construcción (patrón Builder): Director + ConcreteBuilder
 * + Fábrica + Parser + auto-registro de cada entidad en sus subsistemas Observer.
 *
 * Construye desde tokens en memoria (sin depender de un archivo de nivel) para
 * que el test sea determinista aunque cambien los .txt de los niveles.
 */
public class PruebaConstruccionNivel {

    public static void main(String[] args) {
        List<String[]> filas = List.of(
                "P P P P P".split(" "),
                "P J D S P".split(" "),
                "P X-AZUL M-AZUL S P".split(" "),
                "P P P P P".split(" "));

        DirectorDeTablero director = new DirectorDeTablero(new ConstructorTablero());
        Tablero tablero = director.construir(filas);

        // El cerrojo y el muro AZUL deben quedar registrados en el canal (Observer
        // cableado por el auto-registro de cada entidad), con el muro cerrado al inicio.
        GestorDeCanales gestor = tablero.getGestorDeCanales();
        Canal azul = gestor.obtener("AZUL");
        boolean canalOk = azul != null && !azul.estaAbierto();
        System.out.println((canalOk ? "[OK]   " : "[FALLA] ")
                + "Construccion: canal AZUL registrado y muro cerrado al inicio");

        // El jugador quedó registrado (posición fijada por su propio registrarEn).
        boolean jugadorOk = tablero.getJugador() != null
                && tablero.getJugadorFila() == 1 && tablero.getJugadorColumna() == 1;
        System.out.println((jugadorOk ? "[OK]   " : "[FALLA] ")
                + "Construccion: el jugador se registró en su posición inicial");

        // El token 'D' se instancia como Destino y se registra en el gestor de victoria.
        boolean destinoOk = contieneDestino(tablero)
                && tablero.getGestorDeVictoria().getTotalDestinos() == 1;
        System.out.println((destinoOk ? "[OK]   " : "[FALLA] ")
                + "Construccion: el token 'D' se instancia y registra como Destino");

        System.out.println();
        if (canalOk && jugadorOk && destinoOk) {
            System.out.println("PRUEBA DE CONSTRUCCION OK");
        } else {
            System.exit(1);
        }
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
