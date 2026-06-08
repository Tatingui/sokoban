// SueloNormal.java
package modelo;

public class SueloNormal extends EntidadEstatica {
    @Override
    public boolean esTransitable() { return true; }

    @Override
    public String getClaveSprite() { return "suelo"; }
}