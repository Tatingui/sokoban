package modelo;

/**
 * Caja con resistencia limitada. Cada empuje reduce su resistencia en 1;
 * cuando llega a 0 la caja se considera rota.
 *
 * Memento: implementa {@link #capturarEstadoMemento()} / {@link #aplicarEstadoMemento(Object)}
 * para que el Originator ({@link Tablero}) guarde y restaure su resistencia sin
 * conocer su tipo concreto (polimorfismo, sin {@code instanceof}).
 */
public class CajaFragil extends Caja {

    private int resistencia;

    public CajaFragil(int resistencia) {
        this.resistencia = resistencia;
    }

    // ── API pública ──────────────────────────────────────────────────────────

    public void    reducirResistencia() { this.resistencia--; }
    public int     getResistencia()     { return resistencia; }

    /** Cada empuje le quita un punto de resistencia. */
    @Override
    public void alSerEmpujada() { reducirResistencia(); }

    @Override
    public boolean estaRota() { return resistencia <= 0; }

    // ── Memento (estado interno) ──────────────────────────────────────────────

    @Override
    public Object capturarEstadoMemento() { return resistencia; }

    @Override
    public void aplicarEstadoMemento(Object estado) { this.resistencia = (Integer) estado; }

    // ── Entidad ───────────────────────────────────────────────────────────────

    /**
     * El sprite refleja la vida restante (feedback visual para el jugador):
     * sana (3) → dañada (2) → muy dañada (1). Con 0 la caja ya se rompió y se
     * retira de la grilla, por lo que no llega a renderizarse.
     */
    @Override
    public String getClaveImagen() {
        if (resistencia >= 3) return "cajaFragil";
        if (resistencia == 2) return "cajaFragilDaniada";
        return "cajaFragilMuyDaniada";
    }
}
