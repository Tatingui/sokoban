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
    public boolean estaRota()           { return resistencia <= 0; }
    public int     getResistencia()     { return resistencia; }

    // ── Ruta silenciosa — solo usada por Tablero.restaurarEstado ─────────────

    /**
     * Restaura la resistencia al valor capturado en el TableroMemento.
     * Acceso package-private: solo {@link Tablero} puede llamarlo.
     */
    void setResistencia(int resistencia) {
        this.resistencia = resistencia;
    }

    // ── Entidad ───────────────────────────────────────────────────────────────

    @Override
    public String getClaveImagen() { return "cajaFragil"; }
}
