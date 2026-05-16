package modelo;

public class Destino implements ElementoTablero {
    @Override
    public boolean esTransitable() {
        return true; // El casillero de destino es transitable; el jugador y las cajas se posicionan sobre él.
    }
}