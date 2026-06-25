package controlador;

import java.util.List;

/**
 * Mantiene la secuencia ordenada de niveles y el índice del nivel actual.
 * Es lógica pura (sin Swing), por lo que se puede testear de forma aislada del
 * {@link GestorDeNiveles}, que es quien la usa para coordinar la vista.
 */
public class SecuenciaDeNiveles {

    private final List<String> rutas;
    private int actual = 0;

    public SecuenciaDeNiveles(List<String> rutas) {
        if (rutas.isEmpty()) {
            throw new IllegalArgumentException("La secuencia necesita al menos un nivel.");
        }
        this.rutas = List.copyOf(rutas);
    }

    /** Ruta del nivel actual. */
    public String rutaActual() {
        return rutas.get(actual);
    }

    /** ¿Queda algún nivel después del actual? */
    public boolean haySiguiente() {
        return actual < rutas.size() - 1;
    }

    /** Avanza al siguiente nivel si lo hay; si no, no hace nada. */
    public void avanzar() {
        if (haySiguiente()) actual++;
    }

    /** Vuelve al primer nivel. */
    public void reiniciar() {
        actual = 0;
    }

    /** Número del nivel actual en base 1 (para mostrar al usuario). */
    public int numeroActual() {
        return actual + 1;
    }

    /** Cantidad total de niveles. */
    public int total() {
        return rutas.size();
    }
}
