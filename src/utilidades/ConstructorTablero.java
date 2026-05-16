package utilidades;

import modelo.ElementoTablero;
import modelo.Jugador;
import modelo.Tablero;

public class ConstructorTablero {
    private Tablero tablero;
    private FabricaDeEntidades fabrica;

    public ConstructorTablero() {
        this.tablero = new Tablero();
        this.fabrica = FabricaDeEntidades.getInstancia(); // Usamos nuestro Singleton
    }

    public void definirTamanio(int filas, int columnas) {
        tablero.inicializarGrilla(filas, columnas);
    }

    public void procesarFila(int fila, String lineaTexto) {
        for (int columna = 0; columna < lineaTexto.length(); columna++) {
            char c = lineaTexto.charAt(columna);

            // Usamos el Factory Method para crear el elemento
            ElementoTablero elemento = fabrica.crearEntidad(c);
            tablero.colocarElemento(fila, columna, elemento);

            // Si el elemento creado es el jugador, guardamos sus coordenadas
            if (elemento instanceof Jugador) {
                tablero.setPosicionJugador(fila, columna);
            }
        }
    }

    // Método final que devuelve el objeto complejo ya construido
    public Tablero obtenerTablero() {
        return this.tablero;
    }
}