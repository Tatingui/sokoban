package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Subsistema de las cintas transportadoras. Lleva el registro de las celdas-cinta
 * del tablero y, al cerrarse cada turno válido, dispara el arrastre de todas.
 *
 * Para que un mismo objeto no se mueva dos veces en un mismo turno (al pasar de
 * una cinta a otra), primero se toma una "foto" de los ocupantes y recién después
 * se aplica el arrastre sobre cada uno: así cada entidad avanza a lo sumo una celda.
 *
 * El {@link utilidades.ConstructorTablero} registra aquí cada cinta al armar el nivel.
 */
public class GestorDeCintas {

    private final List<int[]> celdas = new ArrayList<>();

    public void registrar(int fila, int columna) {
        celdas.add(new int[]{fila, columna});
    }

    /** Arrastra una celda a cada ocupante de cinta (un paso por turno). */
    public void avanzar(Tablero tablero) {
        // 1 ── Foto de los ocupantes actuales (antes de mover nada).
        List<Arrastre> pendientes = new ArrayList<>();
        for (int[] celda : celdas) {
            EntidadDinamica ocupante = tablero.getOcupante(celda[0], celda[1]);
            if (ocupante != null) {
                pendientes.add(new Arrastre(celda[0], celda[1], ocupante));
            }
        }

        // 2 ── Aplicar el arrastre solo si el ocupante sigue donde lo fotografiamos.
        for (Arrastre a : pendientes) {
            if (tablero.getOcupante(a.fila(), a.columna()) != a.ocupante()) continue;
            tablero.estrategiaSueloEn(a.fila(), a.columna())
                    .alResolverTurno(tablero, a.fila(), a.columna());
        }
    }

    private record Arrastre(int fila, int columna, EntidadDinamica ocupante) { }
}
