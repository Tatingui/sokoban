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
}