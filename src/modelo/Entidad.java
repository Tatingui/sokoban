package modelo;

public abstract class Entidad {
    public abstract boolean esTransitable();

    public abstract boolean esEmpujable();

    /**
     * Le avisa polimórficamente a esta entidad que otro elemento del juego intenta 
     * ingresar a su casillero en una dirección dada.
     * @return true si permite el ingreso, false si bloquea el paso.
     */
    public abstract boolean aceptarIngreso(Tablero tablero, int fila, int col, Direccion direccion);

    /**
     * Clave usada por {@link vista.PanelTablero} para buscar el sprite en su
     * mapa de imágenes. Método primario de renderizado (main branch API).
     */
    public abstract String getClaveImagen();

    /**
     * Alias para {@link #getClaveImagen()} utilizado por el sistema de
     * movimiento / Memento (feature/memento-undo API).
     * Por defecto delega a {@link #getClaveImagen()}; las subclases que
     * necesiten una clave diferente para este accessor pueden sobreescribirlo.
     */
    public String getClaveSprite() {
        return getClaveImagen();
    }
}