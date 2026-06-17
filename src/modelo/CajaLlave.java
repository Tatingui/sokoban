// CajaLlave.java
package modelo;

import java.util.List;

public class CajaLlave extends Caja {

    private final List<String> canales;

    public CajaLlave(List<String> canales) {
        this.canales = canales;
    }

    public boolean perteneceACanal(String canal) {
        return canales.contains(canal);
    }

    public List<String> getCanales() { return canales; }

    /**
     * Devuelve la clave de sprite registrada en PanelTablero:
     *  - llave de un solo canal  → "llave_AZUL"  (o NARANJA, VERDE …)
     *  - llave multicanal        → "llave_MULTICANAL"
     */
    @Override
    public String getClaveImagen() {
        return canales.size() == 1 ? "llave_" + canales.get(0) : "llave_MULTICANAL";
    }
}
