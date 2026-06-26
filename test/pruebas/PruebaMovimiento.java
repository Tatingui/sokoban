package pruebas;

import modelo.CajaFragil;
import modelo.CajaNormal;
import modelo.Destino;
import modelo.Direccion;
import modelo.MotorMovimiento;
import modelo.Pared;
import modelo.ResultadoMovimiento;
import modelo.Sokoban;
import modelo.SueloNormal;
import modelo.Tablero;

/**
 * Prueba manual del motor de movimiento: mirada (giro vs. avance), paredes,
 * empuje válido e inválido, y rotura de la caja frágil.
 */
public class PruebaMovimiento {

    private static int fallos = 0;

    public static void main(String[] args) {
        pruebaGiroAntesDeAvanzar();
        pruebaBloqueoPorPared();
        pruebaEmpujeValido();
        pruebaEmpujeBloqueadoPorPared();
        pruebaEmpujeBloqueadoPorOtraCaja();
        pruebaJugadorSobreDestino();
        pruebaCajaFragilSeRompe();

        System.out.println();
        if (fallos == 0) {
            System.out.println("TODAS LAS PRUEBAS DE MOVIMIENTO PASARON");
        } else {
            System.out.println(fallos + " PRUEBA(S) FALLARON");
            System.exit(1);
        }
    }

    // ── Mirada: girar antes de avanzar ───────────────────────────────────────

    private static void pruebaGiroAntesDeAvanzar() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 3);
        llenarConSuelo(tablero);

        Sokoban jugador = new Sokoban();           // mira ABAJO por defecto
        tablero.colocarElemento(1, 1, jugador);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        MotorMovimiento motor = new MotorMovimiento(tablero);

        check("Giro: el primer toque a una dirección nueva solo gira",
                motor.intentarMover(Direccion.DERECHA) == ResultadoMovimiento.GIRO);
        check("Giro: el jugador no se movió al girar",
                tablero.getJugadorColumna() == 1);
        check("Giro: la mirada quedó en DERECHA",
                jugador.getMirada() == Direccion.DERECHA);
        check("Giro: el segundo toque ya avanza",
                motor.intentarMover(Direccion.DERECHA) == ResultadoMovimiento.CAMINATA);
        check("Giro: el jugador avanzó a la columna 2",
                tablero.getJugadorColumna() == 2);
    }

    // ── Bloqueo por pared ────────────────────────────────────────────────────

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

        jugador.setMirada(Direccion.ARRIBA);
        check("Pared: no puede subir contra una pared",
                motor.intentarMover(Direccion.ARRIBA) == ResultadoMovimiento.SIN_CAMBIO);
        jugador.setMirada(Direccion.DERECHA);
        check("Pared: no puede entrar a la pared de la derecha",
                motor.intentarMover(Direccion.DERECHA) == ResultadoMovimiento.SIN_CAMBIO);
    }

    // ── Empuje válido ────────────────────────────────────────────────────────

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
        jugador.setMirada(Direccion.DERECHA);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        check("Empuje: mueve la caja hacia la derecha",
                motor.intentarMover(Direccion.DERECHA) == ResultadoMovimiento.EMPUJE);
        check("Empuje: el jugador queda en la celda de la caja",
                tablero.getJugadorFila() == 1 && tablero.getJugadorColumna() == 2);
        check("Empuje: la caja avanzó una celda",
                tablero.getEntidad(1, 3) instanceof CajaNormal);
    }

    // ── Empuje bloqueado por pared ───────────────────────────────────────────

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
        jugador.setMirada(Direccion.DERECHA);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        check("Empuje: no empuja la caja contra una pared",
                motor.intentarMover(Direccion.DERECHA) == ResultadoMovimiento.SIN_CAMBIO);
        check("Empuje: el jugador no se movió",
                tablero.getJugadorColumna() == 1);
    }

    // ── Empuje bloqueado por otra caja ───────────────────────────────────────

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
        jugador.setMirada(Direccion.DERECHA);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        check("Empuje: no empuja una caja contra otra",
                motor.intentarMover(Direccion.DERECHA) == ResultadoMovimiento.SIN_CAMBIO);
    }

    // ── Jugador sobre destino ────────────────────────────────────────────────

    private static void pruebaJugadorSobreDestino() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 3);
        llenarConSuelo(tablero);

        Destino destino = new Destino();
        Sokoban jugador = new Sokoban();           // mira ABAJO por defecto

        tablero.colocarElemento(1, 1, destino);
        destino.setObjetoEncima(jugador);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);

        MotorMovimiento motor = new MotorMovimiento(tablero);
        check("Destino: el jugador puede moverse desde un destino",
                motor.intentarMover(Direccion.ABAJO) == ResultadoMovimiento.CAMINATA);
        check("Destino: el suelo especial se conserva",
                tablero.getEntidad(1, 1) instanceof Destino);
        check("Destino: el jugador quedó en la nueva celda",
                tablero.getJugadorFila() == 2 && tablero.getJugadorColumna() == 1);
        check("Destino: la celda destino quedó libre",
                !((Destino) tablero.getEntidad(1, 1)).estaOcupado());
    }

    // ── Caja frágil: se rompe tras agotar su resistencia ─────────────────────

    private static void pruebaCajaFragilSeRompe() {
        Tablero tablero = new Tablero();
        tablero.inicializarGrilla(3, 7);
        llenarConSuelo(tablero);

        Sokoban jugador = new Sokoban();
        CajaFragil caja = new CajaFragil(3);
        tablero.colocarElemento(1, 1, jugador);
        tablero.colocarElemento(1, 2, caja);
        tablero.setJugador(jugador);
        tablero.setPosicionJugador(1, 1);
        jugador.setMirada(Direccion.DERECHA);

        MotorMovimiento motor = new MotorMovimiento(tablero);

        motor.intentarMover(Direccion.DERECHA);  // empuje 1
        check("Fragil: tras 1 empuje resistencia 2", caja.getResistencia() == 2);
        check("Fragil: sprite refleja daño", caja.getClaveImagen().equals("cajaFragilDaniada"));

        motor.intentarMover(Direccion.DERECHA);  // empuje 2
        check("Fragil: tras 2 empujes resistencia 1", caja.getResistencia() == 1);
        check("Fragil: sprite refleja daño grave",
                caja.getClaveImagen().equals("cajaFragilMuyDaniada"));

        ResultadoMovimiento r = motor.intentarMover(Direccion.DERECHA);  // empuje 3 → rotura
        check("Fragil: el 3er empuje es un movimiento válido",
                r == ResultadoMovimiento.EMPUJE);
        check("Fragil: la caja se rompió y desapareció",
                tablero.getOcupante(1, 5) == null);
        check("Fragil: la celda quedó como suelo normal",
                tablero.getEntidad(1, 5) instanceof SueloNormal);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

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
