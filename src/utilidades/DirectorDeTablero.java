package utilidades;

import modelo.Tablero;
import java.util.List;

/**
 * Director del patrón GoF Builder. Conoce la SECUENCIA de pasos necesaria para
 * construir un {@link Tablero} a partir de las filas de tokens de un nivel, y
 * delega cada paso en el {@link ConstructorDeTablero} (Builder) sin conocer los
 * detalles de cómo se crean o cablean las entidades.
 */
public class DirectorDeTablero {

    private final ConstructorDeTablero constructor;

    public DirectorDeTablero(ConstructorDeTablero constructor) {
        this.constructor = constructor;
    }

    /**
     * Orquesta la construcción completa: reinicia, define el tamaño y procesa
     * cada fila en orden, devolviendo el tablero terminado.
     */
    public Tablero construir(List<String[]> filas) {
        constructor.reiniciar();
        constructor.definirTamanio(filas.size(), filas.get(0).length);
        for (int fila = 0; fila < filas.size(); fila++) {
            constructor.procesarFila(fila, filas.get(fila));
        }
        return constructor.obtenerTablero();
    }
}
