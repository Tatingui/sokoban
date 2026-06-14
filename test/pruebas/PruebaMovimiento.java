package pruebas;

import modelo.CajaNormal;
import modelo.Destino;
import modelo.Direccion;
import modelo.MotorMovimiento;
import modelo.Pared;
import modelo.Sokoban;
import modelo.SueloNormal;
import modelo.Tablero;

/**
 * Prueba manual del motor de movimiento: paredes, empuje valido e invalido.
 */
public class PruebaMovimiento {

    private static int fallos = 0;

    public static void main(String[] args) {
        pruebaBloqueoPorPared();
        pruebaEmpujeValido();
        pruebaEmpujeBloqueadoPorPared();
        pruebaEmpujeBloqueadoPorOtraCaja();
        pruebaJugadorSobreDestino();

        System.out.println();
        if (fallos == 0) {
            System.out.println("TODAS LAS PRUEBAS DE MOVIMIENTO PASARON");
        } else {
            System.out.println(fallos + " PRUEBA(S) FALLARON");
            System.exit(1);
        }
    }

    private static void pruebaBloqueoPorPared() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 3);
        llenarConSuelo(tablero);

        Sokoban jugador = new Sokoban();
        tablero.colocarElemento(1, 1, jugador);
        tablero.colocarElemento(0, 1, new Pared());
        tablero.colocarElemento(1, 2, new Pared());
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        check("Pared: no puede subir contra una pared", !motor.intentarMover(Direccion.ARRIBA));
        check("Pared: no puede entrar a la pared de la derecha", !motor.intentarMover(Direccion.DERECHA));
    }

    private static void pruebaEmpujeValido() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 5);
        llenarConSuelo(tablero);

        Sokoban jugador = new Sokoban();
        CajaNormal caja = new CajaNormal();
        tablero.colocarElemento(1, 1, jugador);
        tablero.colocarElemento(1, 2, caja);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        check("Empuje: mueve la caja hacia la derecha", motor.intentarMover(Direccion.DERECHA));
        check("Empuje: el jugador queda en la celda de la caja", tablero.getJugadorFila() == 1 && tablero.getJugadorColumna() == 2);
        check("Empuje: la caja avanzó una celda", tablero.getEntidad(1, 3) instanceof CajaNormal);
    }

    private static void pruebaEmpujeBloqueadoPorPared() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 4);
        llenarConSuelo(tablero);

        Sokoban jugador = new Sokoban();
        tablero.colocarElemento(1, 1, jugador);
        tablero.colocarElemento(1, 2, new CajaNormal());
        tablero.colocarElemento(1, 3, new Pared());
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        check("Empuje: no empuja la caja contra una pared", !motor.intentarMover(Direccion.DERECHA));
        check("Empuje: el jugador no se movió", tablero.getJugadorColumna() == 1);
    }

    private static void pruebaEmpujeBloqueadoPorOtraCaja() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 5);
        llenarConSuelo(tablero);

        Sokoban jugador = new Sokoban();
        tablero.colocarElemento(1, 1, jugador);
        tablero.colocarElemento(1, 2, new CajaNormal());
        tablero.colocarElemento(1, 3, new CajaNormal());
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        check("Empuje: no empuja una caja contra otra", !motor.intentarMover(Direccion.DERECHA));
    }

    private static void pruebaJugadorSobreDestino() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 3);
        llenarConSuelo(tablero);

        Destino destino = new Destino();
        Sokoban jugador = new Sokoban();

        tablero.colocarElemento(1, 1, destino);
        destino.setObjetoEncima(jugador);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        check("Destino: el jugador puede moverse desde un destino", motor.intentarMover(Direccion.ABAJO));
        check("Destino: el suelo especial se conserva", tablero.getEntidad(1, 1) instanceof Destino);
        check("Destino: el jugador quedó en la nueva celda", tablero.getJugadorFila() == 2 && tablero.getJugadorColumna() == 1);
        check("Destino: la celda destino quedó libre", !((Destino) tablero.getEntidad(1, 1)).estaOcupado());
    }

    private static void llenarConSuelo(Tablero tablero) {
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                tablero.colocarElemento(fila, columna, new SueloNormal());
            }
        }
    }

    private static void check(String descripcion, boolean condicion) {
        System.out.println((condicion ? "[OK]   " : "[FALLA] ") + descripcion);
        if (!condicion) {
            fallos++;
        }
    }
}
