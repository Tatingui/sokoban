// Sokoban.java
package modelo;

public class Sokoban extends EntidadDinamica {
    @Override
    public boolean esJugador() { return true; }

    @Override
    public String getClaveImagen() { return "sokoban"; }
}