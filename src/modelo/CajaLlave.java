// CajaLlave.java
package modelo;

import java.util.List;

public class CajaLlave extends Caja {
    private List<String> canales;

    public CajaLlave(List<String> canales) {
        this.canales = canales;
    }

    public boolean perteneceACanal(String canal) {
        return canales.contains(canal);
    }

    public List<String> getCanales() { return canales; }
}