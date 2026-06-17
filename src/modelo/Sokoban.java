// Sokoban.java
package modelo;

public class Sokoban extends EntidadDinamica {

    /** Override to identify the player entity without instanceof checks. */
    @Override
    public boolean esJugador() { return true; }

    @Override
    public String getClaveImagen() { return "sokoban"; }
}
