package modelo;

/**
 * Muro gobernado por un canal de cerrojos. Rol Concrete Observer del patrón
 * Observer: el {@link Canal} lo notifica para que mute su propio estado entre
 * cerrado (infranqueable) y abierto (transitable), sin necesidad de
 * reemplazar la entidad en la grilla.
 *
 * Unifica lo que antes eran dos clases casi idénticas (MuroCerrado /
 * MuroAbierto), eliminando la duplicación: el estado se modela con un boolean.
 *
 * Como puerta transitable, hereda de {@link EntidadEstaticaConOcupante}: cuando
 * está abierta y el jugador o una caja la cruzan, el objeto se guarda encima y
 * el muro persiste en la grilla (no se destruye al ser atravesado).
 *
 * Memento: implementa {@link #capturarEstadoMemento()} / {@link #aplicarEstadoMemento(Object)}
 * para guardar/restaurar su estado abierto/cerrado sin notificar al canal.
 */
public class Muro extends EntidadEstaticaConOcupante implements ObservadorCanal {

    private final String idCanal;
    private boolean abierto;

    public Muro(String idCanal) {
        this(idCanal, false);
    }

    public Muro(String idCanal, boolean abierto) {
        this.idCanal = idCanal;
        this.abierto = abierto;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String  getIdCanal()  { return idCanal; }
    public boolean estaAbierto() { return abierto; }

    // ── Auto-registro ─────────────────────────────────────────────────────────

    @Override
    public void registrarEn(Tablero tablero, int fila, int columna) {
        tablero.getGestorDeCanales().registrarMuro(this);
    }

    // ── ObservadorCanal (ruta normal del juego) ──────────────────────────────

    @Override public void alAbrirCanal()  { abierto = true;  }
    @Override public void alCerrarCanal() { abierto = false; }

    // ── Memento (estado interno, silencioso) ──────────────────────────────────

    @Override
    public Object capturarEstadoMemento() { return abierto; }

    @Override
    public void aplicarEstadoMemento(Object estado) { this.abierto = (Boolean) estado; }

    // ── Entidad ───────────────────────────────────────────────────────────────

    @Override
    public boolean esTransitable() { return abierto && !estaOcupado(); }

    @Override
    public String getClaveImagen() {
        return (abierto ? "muroAbierto_" : "muroCerrado_") + idCanal;
    }
}
