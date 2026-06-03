package modelo;

public abstract class EntidadDinamica extends Entidad {
    @Override
    public boolean esEmpujable() {
        return true; // Todas las dinámicas pueden ser desplazadas
    }

    @Override
    public boolean esTransitable() {
        return false; // Ocupan un espacio físico, no se pueden atravesar
    }
}