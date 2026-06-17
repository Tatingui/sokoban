package modelo;

/**
 * Casillero cerrojo. Hereda de {@link SueloEspecial} (puede tener un objeto
 * encima) y participa del patrón Observer como parte del Subject: reporta a su
 * {@link Canal} cada cambio en su estado de activación.
 *
 * Se activa cuando la {@link CajaLlave} colocada encima pertenece a su canal.
 * Si además es exclusivo, al activarse suprime los otros canales de esa llave.
 *
 * Memento — rutas silenciosas (package-private):
 *  - {@link #setObjetoEncimaSilencioso(EntidadDinamica)}: establece objetoEncima
 *    sin ejecutar la lógica de activación/desactivación del cerrojo.
 *  - {@link #restaurarEstadoSilencioso(boolean, CajaLlave)}: restaura activo y
 *    llaveActiva directamente, sin notificar al canal ni al gestor.
 */
public class CasilleroCerrojo extends SueloEspecial {

    private final String  idCanal;
    private final boolean modoExclusivo;
    private boolean       activo;

    private Canal            canal;
    private GestorDeCanales  gestor;
    /** Llave que activó el cerrojo; se recuerda para liberar la supresión al retirarla. */
    private CajaLlave        llaveActiva;

    public CasilleroCerrojo(String idCanal, boolean modoExclusivo) {
        this.idCanal       = idCanal;
        this.modoExclusivo = modoExclusivo;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String  getIdCanal()      { return idCanal; }
    public boolean isModoExclusivo() { return modoExclusivo; }
    public boolean estaActivo()      { return activo; }

    /** Devuelve la CajaLlave que activó este cerrojo, o null si no está activo. */
    public CajaLlave getLlaveActiva() { return llaveActiva; }

    // ── Configuración (llamada por GestorDeCanales al construir el tablero) ──

    public void asociarGestor(GestorDeCanales gestor, Canal canal) {
        this.gestor = gestor;
        this.canal  = canal;
    }

    // ── Lógica de activación (ruta normal del juego) ─────────────────────────

    /**
     * Punto de entrada de la ruta NORMAL (gameplay): evalúa si el objeto colocado
     * es la llave correcta y activa/desactiva el cerrojo notificando al canal.
     */
    @Override
    public void setObjetoEncima(EntidadDinamica objeto) {
        super.setObjetoEncima(objeto);
        if (esLlaveDelCanal(objeto)) {
            activar((CajaLlave) objeto);
        } else {
            desactivar();
        }
    }

    private boolean esLlaveDelCanal(EntidadDinamica objeto) {
        return objeto instanceof CajaLlave
                && ((CajaLlave) objeto).perteneceACanal(idCanal);
    }

    private void activar(CajaLlave llave) {
        if (activo) return;
        activo      = true;
        llaveActiva = llave;
        if (modoExclusivo && gestor != null) {
            gestor.suprimirOtrosCanales(llave, idCanal);
        }
        notificarCanal();
    }

    private void desactivar() {
        if (!activo) return;
        activo = false;
        if (modoExclusivo && gestor != null && llaveActiva != null) {
            gestor.liberarOtrosCanales(llaveActiva, idCanal);
        }
        llaveActiva = null;
        notificarCanal();
    }

    private void notificarCanal() {
        if (canal != null) canal.evaluarEstado();
    }

    // ── Rutas silenciosas — solo usadas por Tablero.restaurarEstado ──────────

    /**
     * Establece objetoEncima invocando directamente al padre {@link SueloEspecial},
     * sin pasar por la lógica de activación de este cerrojo.
     * Acceso package-private: solo {@link Tablero} puede llamarlo.
     */
    void setObjetoEncimaSilencioso(EntidadDinamica objeto) {
        super.setObjetoEncima(objeto);
    }

    /**
     * Restaura el estado interno (activo, llaveActiva) sin notificar al canal.
     * Debe llamarse DESPUÉS de que las posiciones dinámicas ya hayan sido
     * restauradas en la grilla (para que llaveActiva referencie la instancia
     * correcta ya ubicada).
     * Acceso package-private: solo {@link Tablero} puede llamarlo.
     */
    void restaurarEstadoSilencioso(boolean activo, CajaLlave llaveActiva) {
        this.activo      = activo;
        this.llaveActiva = llaveActiva;
    }

    // ── ElementoTablero ───────────────────────────────────────────────────────

    @Override
    public String getClaveSprite() {
        return "casilleroCerrojo_" + idCanal;
    }

    @Override
    public String getClaveImagen() { return "cerrojo_" + idCanal; }
}
