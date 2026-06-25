package modelo;

public abstract class Entidad {
    public abstract boolean esTransitable();

    public abstract boolean esEmpujable();

    /**
     * Clave usada por {@link vista.PanelTablero} para buscar el sprite en su
     * mapa de imágenes.
     */
    public abstract String getClaveImagen();

    /**
     * Devuelve la entidad dinámica que ocupa esta celda de forma móvil, o
     * {@code null} si no hay ninguna. Permite al {@link MotorMovimiento}
     * consultar el ocupante sin recurrir a {@code instanceof}:
     *  - una entidad estática común no tiene ocupante → {@code null};
     *  - un suelo especial devuelve su {@code objetoEncima};
     *  - una entidad dinámica se devuelve a sí misma.
     */
    public EntidadDinamica getOcupante() {
        return null;
    }
}
