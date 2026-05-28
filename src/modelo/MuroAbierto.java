// MuroAbierto.java
package modelo;

public class MuroAbierto extends EntidadEstatica {
    private String idCanal;

    public MuroAbierto(String idCanal) {
        this.idCanal = idCanal;
    }

    public String getIdCanal() { return idCanal; }

    @Override
    public boolean esTransitable() { return true; }
}