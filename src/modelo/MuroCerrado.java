// MuroCerrado.java
package modelo;

public class MuroCerrado extends EntidadEstatica {
    private String idCanal;

    public MuroCerrado(String idCanal) {
        this.idCanal = idCanal;
    }

    public String getIdCanal() { return idCanal; }

    @Override
    public boolean esTransitable() { return false; }
}