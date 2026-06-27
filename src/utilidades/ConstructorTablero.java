package utilidades;

import modelo.*;

/**
 * ConcreteBuilder del patrón GoF Builder: implementa los pasos de
 * {@link ConstructorDeTablero} construyendo y cableando el {@link Tablero}.
 * Usa la {@link FabricaDeEntidades} (Simple Factory + Singleton) para instanciar
 * cada celda y registra las entidades en los subsistemas Observer del tablero.
 */
public class ConstructorTablero implements ConstructorDeTablero {

    private Tablero tablero;
    private final FabricaDeEntidades fabrica;

    public ConstructorTablero() {
        this.fabrica = FabricaDeEntidades.getInstancia();
        reiniciar();
    }

    @Override
    public void reiniciar() {
        this.tablero = new Tablero();
    }

    @Override
    public void definirTamanio(int filas, int columnas) {
        tablero.inicializarGrilla(filas, columnas);
    }

    @Override
    public void procesarFila(int fila, String[] tokens) {
        for (int columna = 0; columna < tokens.length; columna++) {
            String token = tokens[columna].trim();
            Entidad entidad = fabrica.crearEntidad(token);
            tablero.colocarElemento(fila, columna, entidad);
            
            // Polimorfismo puro: cada objeto se registra a sí mismo de forma autónoma
            entidad.registrarEnTablero(tablero, fila, columna);
        }
    }

    @Override
    public Tablero obtenerTablero() {
        return this.tablero;
    }
}