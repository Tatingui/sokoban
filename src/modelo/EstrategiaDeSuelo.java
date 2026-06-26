package modelo;

/**
 * Strategy (GoF) del comportamiento de un suelo. Encapsula los efectos que un
 * suelo puede producir sobre las entidades, permitiendo variarlos por tipo de
 * piso sin condicionales en el {@link MotorMovimiento}. Cada {@link Entidad}
 * expone su estrategia vía {@link Entidad#getEstrategiaDeSuelo()}.
 *
 * Tiene dos momentos de efecto, ambos con default "sin efecto" para que cada
 * estrategia concreta implemente solo el que le corresponde:
 *  - {@link #alLlegar}: cuando una entidad llega a la celda (ej: deslizamiento).
 *  - {@link #alResolverTurno}: al cerrarse un turno válido (ej: arrastre de cinta).
 */
public interface EstrategiaDeSuelo {

    /**
     * Efecto sobre la entidad que acaba de llegar a (fila, columna) moviéndose
     * en {@code direccion}. Devuelve su posición final de reposo. Sin efecto por
     * defecto: se queda donde llegó.
     */
    default Posicion alLlegar(Tablero tablero, int fila, int columna, Direccion direccion) {
        return new Posicion(fila, columna);
    }

    /**
     * Efecto al resolverse un turno válido sobre la entidad ubicada en
     * (fila, columna). Sin efecto por defecto.
     */
    default void alResolverTurno(Tablero tablero, int fila, int columna) {
    }
}
