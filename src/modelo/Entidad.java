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
     * - una entidad estática común no tiene ocupante → {@code null};
     * - un suelo especial devuelve su {@code objetoEncima};
     * - una entidad dinámica se devuelve a sí misma.
     */
    public EntidadDinamica getOcupante() {
        return null;
    }

    /**
     * Estrategia de suelo de esta celda (patrón Strategy). Por defecto es
     * {@link SueloFijo} (sin efecto); los suelos especiales con comportamiento
     * propio (ej: {@link SueloResbaladizo}) la sobrescriben.
     */
    public EstrategiaDeSuelo getEstrategiaDeSuelo() {
        return SueloFijo.INSTANCIA;
    }

    /**
     * ¿Esta celda detiene el proyectil de un portal? Solo las paredes lo frenan;
     * el disparo atraviesa cajas, puertas y suelos. Por defecto, no detiene.
     */
    public boolean detienePortal() {
        return false;
    }

    /**
     * Permite que cada entidad se auto-registre polimórficamente en los 
     * subsistemas del tablero correspondientes, eliminando el uso de instanceof.
     */
    public void registrarEnTablero(Tablero tablero, int fila, int columna) {
        // Por defecto no hace nada (SueloNormal, Pared, etc.)
    }
}