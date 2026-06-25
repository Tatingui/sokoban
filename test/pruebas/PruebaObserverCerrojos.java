package pruebas;

import modelo.CajaLlave;
import modelo.CasilleroCerrojo;
import modelo.GestorDeCanales;
import modelo.Muro;

/**
 * Prueba manual (sin framework) del patron Observer aplicado a los muros
 * gobernados por canales de cerrojos. Se ejecuta como aplicacion: imprime el
 * resultado de cada escenario y termina con codigo de salida != 0 si algo falla.
 */
public class PruebaObserverCerrojos {

    private static int fallos = 0;

    public static void main(String[] args) {
        escenarioBasico();
        escenarioMultiplesCerrojosMismoCanal();
        escenarioLlaveComodin();
        escenarioComodinUnaPuertaALaVez();

        System.out.println();
        if (fallos == 0) {
            System.out.println("TODAS LAS PRUEBAS PASARON");
        } else {
            System.out.println(fallos + " PRUEBA(S) FALLARON");
            System.exit(1);
        }
    }

    /** 1 cerrojo + 1 muro del mismo canal: la llave abre y al retirarla cierra. */
    private static void escenarioBasico() {
        GestorDeCanales gestor = new GestorDeCanales();
        CasilleroCerrojo cerrojo = new CasilleroCerrojo("AZUL");
        Muro muro = new Muro("AZUL");
        gestor.registrarCerrojo(cerrojo);
        gestor.registrarMuro(muro);

        check("Basico: el muro nace cerrado", !muro.esTransitable());

        cerrojo.setObjetoEncima(CajaLlave.deCanal("AZUL"));
        check("Basico: el muro se abre al colocar la llave", muro.esTransitable());

        cerrojo.setObjetoEncima(null);
        check("Basico: el muro se cierra al retirar la llave", !muro.esTransitable());
    }

    /** Un canal con 2 cerrojos solo abre cuando AMBOS estan activados. */
    private static void escenarioMultiplesCerrojosMismoCanal() {
        GestorDeCanales gestor = new GestorDeCanales();
        CasilleroCerrojo c1 = new CasilleroCerrojo("VERDE");
        CasilleroCerrojo c2 = new CasilleroCerrojo("VERDE");
        Muro muro = new Muro("VERDE");
        gestor.registrarCerrojo(c1);
        gestor.registrarCerrojo(c2);
        gestor.registrarMuro(muro);

        c1.setObjetoEncima(CajaLlave.deCanal("VERDE"));
        check("Multiple: sigue cerrado con un solo cerrojo activo", !muro.esTransitable());

        c2.setObjetoEncima(CajaLlave.deCanal("VERDE"));
        check("Multiple: abre con los dos cerrojos activos", muro.esTransitable());

        c1.setObjetoEncima(null);
        check("Multiple: vuelve a cerrar al desactivar uno", !muro.esTransitable());
    }

    /** Una llave comodín activa cualquier cerrojo; una llave de otro canal no. */
    private static void escenarioLlaveComodin() {
        GestorDeCanales gestor = new GestorDeCanales();
        CasilleroCerrojo cerrojoRojo = new CasilleroCerrojo("ROJA");
        Muro muroRojo = new Muro("ROJA");
        gestor.registrarCerrojo(cerrojoRojo);
        gestor.registrarMuro(muroRojo);

        cerrojoRojo.setObjetoEncima(CajaLlave.comodin());
        check("Comodin: la llave comodín activa cualquier cerrojo (ROJA)", muroRojo.esTransitable());

        // Una llave de canal no coincidente no activa (comportamiento permisivo).
        cerrojoRojo.setObjetoEncima(null);
        cerrojoRojo.setObjetoEncima(CajaLlave.deCanal("AZUL"));
        check("Comodin: una llave de otro canal no activa el cerrojo", !muroRojo.esTransitable());
    }

    /**
     * Sin cerrojos exclusivos, el "una sola puerta abierta a la vez" emerge solo:
     * un comodín está físicamente en un único cerrojo, así que solo abre ese canal.
     * Al moverlo al otro cerrojo, la primera puerta se cierra y se abre la segunda.
     */
    private static void escenarioComodinUnaPuertaALaVez() {
        GestorDeCanales gestor = new GestorDeCanales();
        CasilleroCerrojo cerrojoAzul = new CasilleroCerrojo("AZUL");
        CasilleroCerrojo cerrojoVerde = new CasilleroCerrojo("VERDE");
        Muro muroAzul = new Muro("AZUL");
        Muro muroVerde = new Muro("VERDE");
        gestor.registrarCerrojo(cerrojoAzul);
        gestor.registrarCerrojo(cerrojoVerde);
        gestor.registrarMuro(muroAzul);
        gestor.registrarMuro(muroVerde);

        // El comodín sobre el cerrojo AZUL: abre AZUL, VERDE sigue cerrado.
        cerrojoAzul.setObjetoEncima(CajaLlave.comodin());
        check("UnaALaVez: AZUL abierto con el comodín encima", muroAzul.esTransitable());
        check("UnaALaVez: VERDE sigue cerrado (el comodín no está ahí)", !muroVerde.esTransitable());

        // Movemos el comodín al cerrojo VERDE: AZUL se cierra, VERDE se abre.
        cerrojoAzul.setObjetoEncima(null);
        cerrojoVerde.setObjetoEncima(CajaLlave.comodin());
        check("UnaALaVez: AZUL se cierra al sacar el comodín", !muroAzul.esTransitable());
        check("UnaALaVez: VERDE se abre con el comodín encima", muroVerde.esTransitable());
    }

    private static void check(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[OK]   " : "[FALLA] ") + descripcion);
        if (!condicion) {
            fallos++;
        }
    }
}
