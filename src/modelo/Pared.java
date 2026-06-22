package modelo;

public class Pared extends EntidadEstatica {

    @Override
    public boolean esTransitable() { return false; }

    @Override
    public boolean aceptarIngreso(Tablero tablero, int fila, int col, Direccion direccion) {
        return false; // Bloquea el paso siempre
    }

    @Override
    public String getClaveImagen() { return "pared"; }
}
