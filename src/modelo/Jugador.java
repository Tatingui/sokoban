package modelo;

public class Jugador implements ElementoTablero {
    @Override
    public boolean esTransitable() {
        return false; // El Sokoban ocupa un espacio físico, no se puede atravesar otro jugador
    }
}