package modelo;

/**
 * Un portal vive en el BORDE de una celda, no en la celda misma:
 *  - {@code fila}, {@code columna}: la celda libre pegada a la pared donde quedó;
 *  - {@code lado}: la arista contra la que está (dirección hacia la pared).
 *
 * Una entidad parada en (fila, columna) que se mueve hacia {@code lado} entra al
 * portal y sale por el portal par.
 */
public record Portal(int fila, int columna, Direccion lado) {
}
