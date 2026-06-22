package modelo;

public class SueloNormal extends EntidadEstatica {

    @Override
    public boolean esTransitable() { return true; }

    @Override
    public boolean aceptarIngreso(Tablero tablero, int fila, int col, Direccion direccion) {
        return true; // Al estar vacío, permite el ingreso siempre
    }

    @Override
    public String getClaveImagen() { return "suelo"; }
}