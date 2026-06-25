package modelo;

/**
 * Caja con resistencia limitada. Cada empuje reduce su resistencia en 1;
 * cuando llega a 0 la caja se considera rota.
 *
 * Memento: {@link #setResistencia(int)} permite al Originator ({@link Tablero})
 * restaurar el valor de resistencia capturado en el snapshot sin necesidad de
 * reconstruir la instancia desde cero.
 * Acceso package-private: solo clases del paquete modelo pueden invocarlo.
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

    // ── Ruta silenciosa — solo usada por Tablero.restaurarEstado ─────────────

    /**
     * Restaura la resistencia al valor capturado en el TableroMemento.
     * Acceso package-private: solo {@link Tablero} puede llamarlo.
     */
    void setResistencia(int resistencia) {
        this.resistencia = resistencia;
    }

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
