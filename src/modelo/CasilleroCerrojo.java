// CasilleroCerrojo.java
package modelo;

public class CasilleroCerrojo extends SueloEspecial {
    private String idCanal;
    private boolean modoExclusivo;

    public CasilleroCerrojo(String idCanal, boolean modoExclusivo) {
        this.idCanal = idCanal;
        this.modoExclusivo = modoExclusivo;
    }

    public String getIdCanal() { return idCanal; }
    public boolean isModoExclusivo() { return modoExclusivo; }
}