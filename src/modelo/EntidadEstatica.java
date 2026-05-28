package modelo;

public abstract class EntidadEstatica extends Entidad {
    @Override
    public boolean esEmpujable() {
        return false; // Ninguna entidad estática reacciona a fuerzas
    }
}