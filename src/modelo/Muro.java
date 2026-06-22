package modelo;

public class Muro extends EntidadEstatica implements ObservadorCanal {

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

    // ── ObservadorCanal (ruta normal del juego) ──────────────────────────────

    @Override public void alAbrirCanal()  { abierto = true;  }
    @Override public void alCerrarCanal() { abierto = false; }

    // ── Ruta silenciosa — solo usada por Tablero.restaurarEstado ─────────────

    void restaurarEstadoSilencioso(boolean abierto) {
        this.abierto = abierto;
    }

    // ── Entidad ───────────────────────────────────────────────────────────────

    @Override
    public boolean esTransitable() { return abierto; }

    @Override
    public boolean aceptarIngreso(Tablero tablero, int fila, int col, Direccion direccion) {
        return abierto; // Pasa si el canal del Observer abrió la puerta
    }

    @Override
    public String getClaveSprite() {
        return (abierto ? "muroAbierto_" : "muroCerrado_") + idCanal;
    }

    @Override
    public String getClaveImagen() {
        return (abierto ? "muroAbierto_" : "muroCerrado_") + idCanal;
    }
}