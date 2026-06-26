package modelo;

public abstract class EntidadDinamica extends Entidad {
    @Override
    public boolean esEmpujable() {
        return true;
    }

    @Override
    public boolean esTransitable() {
        return false;
    }

    public boolean esJugador() {
        return false;
    }

    /** Una entidad dinámica es su propio ocupante en la celda que pisa. */
    @Override
    public EntidadDinamica getOcupante() {
        return this;
    }

    /**
     * Reacción de la dinámica al ser empujada. Por defecto no pasa nada;
     * {@link CajaFragil} la sobrescribe para perder resistencia. Permite cablear
     * la rotura sin {@code instanceof} en el {@link MotorMovimiento}.
     */
    public void alSerEmpujada() { }

    /** Indica si la dinámica debe desaparecer tras el empuje. Solo la caja frágil se rompe. */
    public boolean estaRota() { return false; }

    /**
     * ¿Esta dinámica cuenta como caja-objetivo cuando reposa sobre un destino?
     * Solo las cajas que el jugador debe llevar a los destinos (normal y frágil);
     * no las llaves ni el propio Sokoban.
     */
    public boolean esObjetivoDeNivel() { return false; }
}