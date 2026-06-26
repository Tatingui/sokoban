// MainTestSonido.java
import sonido.GestorDeSonido;

/**
 * Prueba de sonido AISLADA (sin juego ni ventana). Reproduce la música de fondo
 * y cada efecto en orden, con pausas, para verificar que el audio del juego se
 * escucha. Si acá NO se escucha nada pero el resto de la PC sí suena, el problema
 * es de la salida de audio de Java (ej: el mezclador de volumen de Windows mutea
 * la app de Java), no del juego.
 */
public class MainTestSonido {
    public static void main(String[] args) throws InterruptedException {
        GestorDeSonido sonido = GestorDeSonido.getInstancia();

        System.out.println(">> Música de fondo (debería sonar en bucle)...");
        sonido.reproducirMusicaFondo();
        Thread.sleep(3000);

        System.out.println(">> Caminar");        sonido.caminar();        Thread.sleep(1500);
        System.out.println(">> Mover caja");      sonido.moverCaja();      Thread.sleep(1500);
        System.out.println(">> Caja en destino"); sonido.cajaEnDestino();  Thread.sleep(1500);
        System.out.println(">> Abrir portal");    sonido.abrirPortal();    Thread.sleep(1500);
        System.out.println(">> Victoria");        sonido.victoria();       Thread.sleep(3000);

        System.out.println("Fin del test de sonido.");
        System.exit(0);
    }
}
