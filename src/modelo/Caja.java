// Caja.java  (abstracta)
package modelo;

public abstract class Caja extends EntidadDinamica {

    /** Las cajas cuentan como objetivo de nivel por defecto; la llave lo desactiva. */
    @Override
    public boolean esObjetivoDeNivel() { return true; }
}
