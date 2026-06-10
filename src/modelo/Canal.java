package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Rol Subject (Observable) del patrón Observer.
 *
 * Agrupa todos los cerrojos y muros de un mismo canal identificado (ej: AZUL).
 * Cuando cambia el estado de activación de alguno de sus cerrojos se invoca
 * {@link #evaluarEstado()}: si TODOS los cerrojos del canal están activados (y
 * el canal no fue suprimido por un cerrojo exclusivo) se notifica la apertura a
 * los observadores; en caso contrario se notifica el cierre.
 *
 * Memento — ruta silenciosa (package-private):
 *  - {@link #restaurarEstadoSilencioso(boolean, boolean)}: restaura abierto y
 *    suprimido directamente, sin recalcular ni notificar a los observadores.
 */
public class Canal {

    private final String id;
    private final List<CasilleroCerrojo> cerrojos    = new ArrayList<>();
    private final List<ObservadorCanal>  observadores = new ArrayList<>();
    private boolean abierto   = false;
    /** Suprimido por un cerrojo exclusivo de otra llave multicanal. */
    private boolean suprimido = false;

    public Canal(String id) {
        this.id = id;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String  getId()         { return id; }
    public boolean estaAbierto()   { return abierto; }
    public boolean isSuprimido()   { return suprimido; }

    // ── Configuración (usada por GestorDeCanales al construir el tablero) ────

    public void registrarCerrojo(CasilleroCerrojo cerrojo) {
        if (!cerrojos.contains(cerrojo)) cerrojos.add(cerrojo);
    }

    public void suscribir(ObservadorCanal observador) {
        if (!observadores.contains(observador)) {
            observadores.add(observador);
            // Sincroniza al recién suscrito con el estado actual del canal.
            if (abierto) observador.alAbrirCanal();
            else         observador.alCerrarCanal();
        }
    }

    // ── Lógica del Observer (ruta normal del juego) ──────────────────────────

    /**
     * Recalcula el estado del canal y notifica a los observadores solo si hubo
     * un cambio real, evitando notificaciones redundantes.
     */
    public void evaluarEstado() {
        boolean debeAbrir = !suprimido
                && !cerrojos.isEmpty()
                && todosLosCerrojosActivos();

        if (debeAbrir && !abierto) {
            abierto = true;
            notificar(true);
        } else if (!debeAbrir && abierto) {
            abierto = false;
            notificar(false);
        }
    }

    /** Usado por el modo exclusivo para forzar el cierre de otros canales. */
    public void setSuprimido(boolean suprimido) {
        if (this.suprimido != suprimido) {
            this.suprimido = suprimido;
            evaluarEstado();
        }
    }

    // ── Ruta silenciosa — solo usada por Tablero.restaurarEstado ─────────────

    /**
     * Restaura abierto y suprimido directamente, sin llamar a {@link #evaluarEstado()}
     * ni notificar a los observadores ({@link Muro}).
     * Debe llamarse DESPUÉS de restaurar los estados de cerrojos y ANTES de
     * restaurar los estados de muros, para que la cadena Observable → Observer
     * no se re-dispare con valores intermedios.
     * Acceso package-private: solo {@link Tablero} puede llamarlo.
     */
    void restaurarEstadoSilencioso(boolean abierto, boolean suprimido) {
        this.abierto   = abierto;
        this.suprimido = suprimido;
    }

    // ── Helpers privados ─────────────────────────────────────────────────────

    private boolean todosLosCerrojosActivos() {
        for (CasilleroCerrojo cerrojo : cerrojos) {
            if (!cerrojo.estaActivo()) return false;
        }
        return true;
    }

    private void notificar(boolean apertura) {
        for (ObservadorCanal observador : observadores) {
            if (apertura) observador.alAbrirCanal();
            else          observador.alCerrarCanal();
        }
    }
}
