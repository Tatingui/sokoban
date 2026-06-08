package modelo;

public abstract class Entidad {
    public abstract boolean esTransitable();
    public abstract boolean esEmpujable();
    public abstract String getClaveSprite();
}