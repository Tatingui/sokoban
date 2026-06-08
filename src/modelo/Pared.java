// Pared.java
package modelo;

public class Pared extends EntidadEstatica {
    @Override
    public boolean esTransitable() { return false; }

     @Override
    public String getClaveSprite() { return "pared"; }
   
}

   