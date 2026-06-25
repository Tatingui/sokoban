package modelo;

/**
 * Casillero cerrojo. Hereda de {@link SueloEspecial} (puede tener un objeto
 * encima) y participa del patrón Observer como parte del Subject: reporta a su
 * {@link Canal} cada cambio en su estado de activación.
 *
 * Se activa cuando la {@link CajaLlave} colocada encima pertenece a su canal
 * (una llave comodín pertenece a todos los canales).
 *
 * Memento — ruta silenciosa (package-private):
 *  - {@link #restaurarEstadoSilencioso(boolean)}: restaura el flag {@code activo}
 *    sin notificar al canal. La reubicación del objeto encima usa la ruta
 *    silenciosa heredada de {@link EntidadEstaticaConOcupante}.
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

    // ── Configuración (llamada por GestorDeCanales al construir el tablero) ──

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
        if (esLlaveDelCanal(objeto)) {
            activar();
        } else {
            desactivar();
        }
    }

    private boolean esLlaveDelCanal(EntidadDinamica objeto) {
        return objeto instanceof CajaLlave llave && llave.perteneceACanal(idCanal);
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

    // ── Ruta silenciosa — solo usada por Tablero.restaurarEstado ─────────────

    /**
     * Restaura el flag {@code activo} sin notificar al canal.
     * Acceso package-private: solo {@link Tablero} puede llamarlo.
     */
    void restaurarEstadoSilencioso(boolean activo) {
        this.activo = activo;
    }

    // ── Entidad ───────────────────────────────────────────────────────────────

    @Override
    public String getClaveImagen() { return "cerrojo_" + idCanal; }
}
