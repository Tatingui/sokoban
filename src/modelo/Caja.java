// Caja.java  (abstracta)
package modelo;

public abstract class Caja extends EntidadDinamica {

@Override
public boolean aceptarIngreso(Tablero tablero, int fila, int col, Direccion direccion) {
    return false; // Parche temporal para compilar el Paso 0
}

}