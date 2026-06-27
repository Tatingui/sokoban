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
     * Auto-registro en los subsistemas del tablero al construir el nivel
     * (cableado del patrón Observer, posición del jugador, etc.). Cada entidad
     * sabe dónde inscribirse; por defecto no se inscribe en ningún lado. Evita
     * que el {@link utilidades.ConstructorTablero} dispatch por tipo con instanceof.
     */
    public void registrarEn(Tablero tablero, int fila, int columna) {
        // por defecto, la entidad no necesita registrarse
    }

    /**
     * ¿Esta celda puede alojar una dinámica encima sin perder su identidad
     * (modelo de "casillero intermedio")? Lo hacen los suelos especiales y el
     * muro; por defecto no.
     */
    public boolean puedeAlojar() {
        return false;
    }

    /** Coloca una dinámica encima (ruta de gameplay). Solo aplica si {@link #puedeAlojar()}. */
    public void colocarOcupante(EntidadDinamica dinamica) {
        // por defecto la celda no aloja: no hace nada
    }

    /** Coloca una dinámica encima sin disparar lógica (ruta de Memento). */
    public void colocarOcupanteSilencioso(EntidadDinamica dinamica) {
        // por defecto la celda no aloja: no hace nada
    }

    /**
     * Captura el estado interno mutable de la entidad para el Memento (ej:
     * resistencia de una caja frágil, activación de un cerrojo, apertura de un
     * muro). Devuelve {@code null} si la entidad no tiene estado que guardar.
     */
    public Object capturarEstadoMemento() {
        return null;
    }

    /** Restaura el estado interno capturado por {@link #capturarEstadoMemento()} (silencioso). */
    public void aplicarEstadoMemento(Object estado) {
        // por defecto no hay estado que restaurar
    }
}
