package modelo;

public class Suelo implements ElementoTablero {
    @Override
    public boolean esTransitable() {
        return true; // El suelo vacío permite que se camine o se empujen cosas sobre él
    }
}