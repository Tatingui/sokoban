package pruebas;

import modelo.CajaLlave;
import modelo.CasilleroCerrojo;
import modelo.GestorDeCanales;
import modelo.Muro;

import java.util.Arrays;
import java.util.Collections;

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
        escenarioLlaveMulticanal();
        escenarioCerrojoExclusivo();

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
        CasilleroCerrojo cerrojo = new CasilleroCerrojo("AZUL", false);
        Muro muro = new Muro("AZUL");
        gestor.registrarCerrojo(cerrojo);
        gestor.registrarMuro(muro);

        check("Basico: el muro nace cerrado", !muro.esTransitable());

        cerrojo.setObjetoEncima(new CajaLlave(Collections.singletonList("AZUL")));
        check("Basico: el muro se abre al colocar la llave", muro.esTransitable());

        cerrojo.setObjetoEncima(null);
        check("Basico: el muro se cierra al retirar la llave", !muro.esTransitable());
    }

    /** Un canal con 2 cerrojos solo abre cuando AMBOS estan activados. */
    private static void escenarioMultiplesCerrojosMismoCanal() {
        GestorDeCanales gestor = new GestorDeCanales();
        CasilleroCerrojo c1 = new CasilleroCerrojo("VERDE", false);
        CasilleroCerrojo c2 = new CasilleroCerrojo("VERDE", false);
        Muro muro = new Muro("VERDE");
        gestor.registrarCerrojo(c1);
        gestor.registrarCerrojo(c2);
        gestor.registrarMuro(muro);

        c1.setObjetoEncima(new CajaLlave(Collections.singletonList("VERDE")));
        check("Multiple: sigue cerrado con un solo cerrojo activo", !muro.esTransitable());

        c2.setObjetoEncima(new CajaLlave(Collections.singletonList("VERDE")));
        check("Multiple: abre con los dos cerrojos activos", muro.esTransitable());

        c1.setObjetoEncima(null);
        check("Multiple: vuelve a cerrar al desactivar uno", !muro.esTransitable());
    }

    /** Una llave AZUL+ROJA activa un cerrojo ROJA; en uno de otro canal no activa. */
    private static void escenarioLlaveMulticanal() {
        GestorDeCanales gestor = new GestorDeCanales();
        CasilleroCerrojo cerrojoRojo = new CasilleroCerrojo("ROJA", false);
        Muro muroRojo = new Muro("ROJA");
        gestor.registrarCerrojo(cerrojoRojo);
        gestor.registrarMuro(muroRojo);

        CajaLlave multicanal = new CajaLlave(Arrays.asList("AZUL", "ROJA"));
        cerrojoRojo.setObjetoEncima(multicanal);
        check("Multicanal: la llave AZUL+ROJA activa el cerrojo ROJA", muroRojo.esTransitable());

        // Una llave de canal no coincidente no activa (comportamiento permisivo).
        cerrojoRojo.setObjetoEncima(null);
        cerrojoRojo.setObjetoEncima(new CajaLlave(Collections.singletonList("AZUL")));
        check("Multicanal: una llave de otro canal no activa el cerrojo", !muroRojo.esTransitable());
    }

    /** Cerrojo exclusivo: al activar AZUL con una llave AZUL+ROJA, ROJA se cierra. */
    private static void escenarioCerrojoExclusivo() {
        GestorDeCanales gestor = new GestorDeCanales();
        CasilleroCerrojo cerrojoAzulExc = new CasilleroCerrojo("AZUL", true);
        CasilleroCerrojo cerrojoRojo = new CasilleroCerrojo("ROJA", false);
        Muro muroAzul = new Muro("AZUL");
        Muro muroRojo = new Muro("ROJA");
        gestor.registrarCerrojo(cerrojoAzulExc);
        gestor.registrarCerrojo(cerrojoRojo);
        gestor.registrarMuro(muroAzul);
        gestor.registrarMuro(muroRojo);

        // Una llave roja dedicada mantiene abierto el canal ROJA.
        cerrojoRojo.setObjetoEncima(new CajaLlave(Collections.singletonList("ROJA")));
        check("Exclusivo: ROJA abierto por su llave dedicada", muroRojo.esTransitable());

        // La llave multicanal en el cerrojo AZUL exclusivo abre AZUL y suprime ROJA.
        cerrojoAzulExc.setObjetoEncima(new CajaLlave(Arrays.asList("AZUL", "ROJA")));
        check("Exclusivo: AZUL se abre", muroAzul.esTransitable());
        check("Exclusivo: ROJA se cierra pese a su llave dedicada", !muroRojo.esTransitable());

        // Al retirar la llave del cerrojo exclusivo se libera ROJA.
        cerrojoAzulExc.setObjetoEncima(null);
        check("Exclusivo: AZUL se cierra al retirar la llave", !muroAzul.esTransitable());
        check("Exclusivo: ROJA se reabre al liberar la supresion", muroRojo.esTransitable());
    }

    private static void check(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[OK]   " : "[FALLA] ") + descripcion);
        if (!condicion) {
            fallos++;
        }
    }
}
