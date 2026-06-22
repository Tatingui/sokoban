// Sokoban.java
package modelo;

public class Sokoban extends EntidadDinamica {

    /** Override to identify the player entity without instanceof checks. */
    @Override
    public boolean esJugador() { return true; }

    @Override
    public String getClaveImagen() { return "sokoban"; }
    
@Override
public boolean aceptarIngreso(Tablero tablero, int fila, int col, Direccion direccion) {
    return false; // El jugador no puede ser atravesado por otra entidad
}

}

