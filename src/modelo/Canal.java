package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Rol Subject (Observable) del patrón Observer.
 *
 * Agrupa todos los cerrojos y muros de un mismo canal identificado (ej: AZUL).
 * Cuando cambia el estado de activación de alguno de sus cerrojos se invoca
 * {@link #evaluarEstado()}: si TODOS los cerrojos del canal están activados se
 * notifica la apertura a los observadores; en caso contrario, el cierre.
 *
 * Memento — ruta silenciosa (package-private):
 *  - {@link #restaurarEstadoSilencioso(boolean)}: restaura abierto directamente,
 *    sin recalcular ni notificar a los observadores.
 */
public class Canal {

    private final String id;
    private final List<CasilleroCerrojo> cerrojos    = new ArrayList<>();
    private final List<ObservadorCanal>  observadores = new ArrayList<>();
    private boolean abierto = false;

    public Canal(String id) {
        this.id = id;
    }

    // ── Getters ──────────────────────────────────────────────────────────────

    public String  getId()       { return id; }
    public boolean estaAbierto() { return abierto; }

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
        boolean debeAbrir = !cerrojos.isEmpty() && todosLosCerrojosActivos();

        if (debeAbrir && !abierto) {
            abierto = true;
            notificar(true);
        } else if (!debeAbrir && abierto) {
            abierto = false;
            notificar(false);
        }
    }

    // ── Ruta silenciosa — solo usada por Tablero.restaurarEstado ─────────────

    /**
     * Restaura abierto directamente, sin llamar a {@link #evaluarEstado()} ni
     * notificar a los observadores ({@link Muro}).
     * Debe llamarse DESPUÉS de restaurar los estados de cerrojos y ANTES de
     * restaurar los estados de muros, para que la cadena Observable → Observer
     * no se re-dispare con valores intermedios.
     * Acceso package-private: solo {@link Tablero} puede llamarlo.
     */
    void restaurarEstadoSilencioso(boolean abierto) {
        this.abierto = abierto;
    }

    // ── Helpers privados ─────────────────────────────────────────────────────

    /** Cantidad de cerrojos de este canal que están activados. */
    public int contarCerrojosActivos() {
        int activos = 0;
        for (CasilleroCerrojo cerrojo : cerrojos) {
            if (cerrojo.estaActivo()) activos++;
        }
        return activos;
    }

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
