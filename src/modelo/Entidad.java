package modelo;

public abstract class Entidad {
    public abstract boolean esTransitable();

    public abstract boolean esEmpujable();

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
