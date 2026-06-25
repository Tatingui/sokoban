package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Observador concreto del patrón Observer para la condición de victoria.
 *
 * Se suscribe a cada {@link Destino} del tablero (rol Subject) y mantiene un
 * contador O(1) de cuántos están satisfechos. Reemplaza el antiguo polling que
 * recorría toda la grilla en cada movimiento: ahora solo reacciona a las
 * transiciones que los destinos notifican.
 *
 * El {@link Tablero} lo expone y el {@link utilidades.ConstructorTablero}
 * registra en él cada destino al armar el nivel, igual que hace el
 * {@link GestorDeCanales} con cerrojos y muros.
 *
 * Memento — ruta silenciosa (package-private):
 *  - {@link #recontar()}: reconstruye el contador desde el estado actual de los
 *    destinos tras una restauración, sin disparar notificaciones.
 */
public class GestorDeVictoria implements ObservadorDestino {

    private final List<Destino> destinos = new ArrayList<>();
    private int satisfechos = 0;

    // ── Configuración (usada por ConstructorTablero al construir el tablero) ──

    public void registrarDestino(Destino destino) {
        if (!destinos.contains(destino)) {
            destinos.add(destino);
            destino.suscribir(this);
            // Sincroniza el contador con el estado inicial del destino.
            if (destino.estaSatisfecho()) satisfechos++;
        }
    }

    // ── Observer (ruta normal del juego) ─────────────────────────────────────

    @Override public void alSatisfacer()   { satisfechos++; }
    @Override public void alInsatisfacer() { satisfechos--; }

    // ── Consultas de estado ──────────────────────────────────────────────────

    /** Cajas válidas actualmente sobre destinos. */
    public int getCajasEnDestino() { return satisfechos; }

    /** Total de destinos del nivel. */
    public int getTotalDestinos() { return destinos.size(); }

    /** El nivel está resuelto cuando todos los destinos están satisfechos. */
    public boolean nivelResuelto() {
        return !destinos.isEmpty() && satisfechos == destinos.size();
    }

    // ── Ruta silenciosa — solo usada por Tablero.restaurarEstado ─────────────

    /**
     * Recalcula el contador desde el estado actual de cada destino, sin emitir
     * notificaciones. Debe llamarse al final de una restauración de Memento,
     * cuando las cajas ya fueron reubicadas por la ruta silenciosa.
     */
    void recontar() {
        satisfechos = 0;
        for (Destino destino : destinos) {
            destino.restaurarSatisfechoSilencioso();
            if (destino.estaSatisfecho()) satisfechos++;
        }
    }
}
