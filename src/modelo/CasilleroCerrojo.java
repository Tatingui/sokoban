package modelo;

/**
 * Casillero cerrojo. Hereda de {@link SueloEspecial} (puede tener un objeto
 * encima) y participa del patrón Observer como parte del Subject: reporta a su
 * {@link Canal} cada cambio en su estado de activación.
 *
 * Se activa cuando la dinámica colocada encima es una llave de su canal
 * ({@link EntidadDinamica#activaCerrojo(String)}; el comodín pertenece a todos).
 *
 * Memento: implementa {@link #capturarEstadoMemento()} / {@link #aplicarEstadoMemento(Object)}
 * para guardar/restaurar el flag {@code activo} sin notificar al canal.
 */
public class CasilleroCerrojo extends SueloEspecial {

    private final String idCanal;
    private boolean      activo;
    private Canal        canal;

    public CasilleroCerrojo(String idCanal) {
        this.idCanal = idCanal;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String  getIdCanal() { return idCanal; }
    public boolean estaActivo() { return activo; }

    // ── Auto-registro y configuración ────────────────────────────────────────

    @Override
    public void registrarEn(Tablero tablero, int fila, int columna) {
        tablero.getGestorDeCanales().registrarCerrojo(this);
    }

    public void asociarCanal(Canal canal) {
        this.canal = canal;
    }

    // ── Lógica de activación (ruta normal del juego) ─────────────────────────

    /**
     * Punto de entrada de la ruta NORMAL (gameplay): evalúa si el objeto colocado
     * es una llave del canal y activa/desactiva el cerrojo notificando al canal.
     */
    @Override
    public void setObjetoEncima(EntidadDinamica objeto) {
        super.setObjetoEncima(objeto);
        if (objeto != null && objeto.activaCerrojo(idCanal)) {
            activar();
        } else {
            desactivar();
        }
    }

    private void activar() {
        if (activo) return;
        activo = true;
        notificarCanal();
    }

    private void desactivar() {
        if (!activo) return;
        activo = false;
        notificarCanal();
    }

    private void notificarCanal() {
        if (canal != null) canal.evaluarEstado();
    }

    // ── Memento (estado interno, silencioso) ──────────────────────────────────

    @Override
    public Object capturarEstadoMemento() { return activo; }

    @Override
    public void aplicarEstadoMemento(Object estado) { this.activo = (Boolean) estado; }

    // ── Entidad ───────────────────────────────────────────────────────────────

    @Override
    public String getClaveImagen() { return "cerrojo_" + idCanal; }
}
