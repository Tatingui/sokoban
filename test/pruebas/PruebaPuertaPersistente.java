package pruebas;

import modelo.*;

/**
 * Prueba de regresión del bug de las puertas: un muro abierto debe persistir al
 * ser atravesado (no convertirse en SueloNormal) y volver a cerrarse si se retira
 * la llave del cerrojo. Cubre además que el Memento preserve un ocupante parado
 * sobre la puerta.
 *
 * Layout (3x4), canal AZUL:
 *   P P P P
 *   J M-AZUL S P     (jugador, puerta, suelo)
 *   X-AZUL P P P     (cerrojo debajo del jugador)
 */
public class PruebaPuertaPersistente {

    private static int fallos = 0;

    public static void main(String[] args) {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 4);
        rellenarSuelo(tablero);

        Sokoban jugador = new Sokoban();
        Muro muro = new Muro("AZUL");
        CasilleroCerrojo cerrojo = new CasilleroCerrojo("AZUL");
        CajaLlave llave = CajaLlave.deCanal("AZUL");

        tablero.colocarElemento(1, 0, jugador);
        tablero.colocarElemento(1, 1, muro);
        tablero.colocarElemento(2, 0, cerrojo);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 0);

        tablero.getGestorDeCanales().registrarCerrojo(cerrojo);
        tablero.getGestorDeCanales().registrarMuro(muro);

        MotorMovimiento motor = new MotorMovimiento(tablero);

        check("Inicio: la puerta arranca cerrada", !muro.esTransitable());

        // Abrir la puerta: colocar la llave AZUL sobre el cerrojo AZUL
        cerrojo.setObjetoEncima(llave);
        check("Apertura: la puerta se abre con la llave", muro.esTransitable());

        // Cruzar la puerta: jugador (1,0) -> (1,1) [sobre el muro abierto]
        jugador.setMirada(Direccion.DERECHA);
        check("Cruce: entrar a la puerta es un movimiento",
                motor.intentarMover(Direccion.DERECHA) == ResultadoMovimiento.MOVIMIENTO);
        check("Cruce: el muro PERSISTE bajo el jugador",
                tablero.getEntidad(1, 1) instanceof Muro);
        check("Cruce: el jugador está encima del muro",
                tablero.getOcupante(1, 1) == jugador);

        // Memento: guardar con el jugador sobre la puerta
        TableroMemento snap = tablero.guardarEstado(1);

        // Salir de la puerta: jugador (1,1) -> (1,2)
        motor.intentarMover(Direccion.DERECHA);
        check("Salida: la puerta SIGUE SIENDO un muro (no SueloNormal)",
                tablero.getEntidad(1, 1) instanceof Muro);
        check("Salida: la puerta vuelve a ser transitable (vacía y abierta)",
                muro.esTransitable());

        // Undo: el jugador debe volver a estar sobre la puerta
        tablero.restaurarEstado(snap);
        check("Memento: tras el undo el jugador vuelve sobre la puerta",
                tablero.getOcupante(1, 1) == jugador);
        check("Memento: la puerta sigue abierta y ocupada",
                muro.estaAbierto() && muro.estaOcupado());

        // Retirar la llave del cerrojo: la puerta debe cerrarse
        // (primero saco al jugador de encima para evaluar la transitabilidad limpia)
        motor.intentarMover(Direccion.DERECHA);          // jugador sale a (1,2)
        cerrojo.setObjetoEncima(null);                   // se retira la llave
        check("Cierre: al quitar la llave la puerta se cierra",
                !muro.esTransitable() && !muro.estaAbierto());
        check("Cierre: el muro sigue presente en la grilla",
                tablero.getEntidad(1, 1) instanceof Muro);

        System.out.println();
        if (fallos == 0) {
            System.out.println("TODAS LAS PRUEBAS DE PUERTA PERSISTENTE PASARON");
        } else {
            System.out.println(fallos + " PRUEBA(S) FALLARON");
            System.exit(1);
        }
    }

    private static void rellenarSuelo(Tablero tablero) {
        for (int f = 0; f < tablero.getFilas(); f++)
            for (int c = 0; c < tablero.getColumnas(); c++)
                tablero.colocarElemento(f, c, new SueloNormal());
    }

    private static void check(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[OK]   " : "[FALLA] ") + descripcion);
        if (!condicion) fallos++;
    }
}
