package utilidades;

import modelo.Tablero;

/**
 * Builder (GoF) para construir un {@link Tablero} paso a paso a partir de los
 * datos de un nivel.
 *
 * Declara los pasos de construcción; el {@link DirectorDeTablero} conoce el orden
 * en que se invocan y el ConcreteBuilder ({@link ConstructorTablero}) los
 * implementa y entrega el producto final.
 */
public interface ConstructorDeTablero {

    /** Comienza la construcción de un tablero nuevo, descartando el anterior. */
    void reiniciar();

    /** Define las dimensiones de la grilla. */
    void definirTamanio(int filas, int columnas);

    /** Coloca y cablea las entidades de una fila a partir de sus tokens. */
    void procesarFila(int fila, String[] tokens);

    /** Devuelve el producto construido. */
    Tablero obtenerTablero();
}
