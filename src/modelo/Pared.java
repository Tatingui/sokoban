// Pared.java
package modelo;

public class Pared extends EntidadEstatica {

    @Override
    public boolean esTransitable() { return false; }

    /** La pared es lo único que frena el proyectil de un portal. */
    @Override
    public boolean detienePortal() { return true; }

    @Override
    public String getClaveImagen() { return "pared"; }
}
