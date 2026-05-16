package modelo;

public class Pared implements ElementoTablero {
    @Override
    public boolean esTransitable() {
        return false; // Las paredes bloquean el paso por completo
    }
}