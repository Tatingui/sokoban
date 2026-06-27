// Destino.java
package modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Casilla destino. Hereda de {@link SueloEspecial} (puede tener un objeto
 * encima) y actúa como Subject del patrón Observer aplicado a la victoria:
 * reporta a sus {@link ObservadorDestino} cada vez que pasa a estar satisfecho
 * (tiene una caja válida encima) o deja de estarlo.
 *
 * Memento — ruta silenciosa (package-private):
 *  - {@link #restaurarSatisfechoSilencioso()}: recalcula el flag interno desde
 *    el objeto restaurado sin notificar a los observadores. La reubicación del
 *    objeto encima la hace {@link SueloEspecial#setObjetoEncimaSilencioso}.
 */
public class Destino extends SueloEspecial {

    private final List<ObservadorDestino> observadores = new ArrayList<>();
    private boolean satisfecho = false;

    // ── Configuración (llamada por GestorDeVictoria al construir el tablero) ──

    public void suscribir(ObservadorDestino observador) {
        if (!observadores.contains(observador)) observadores.add(observador);
    }

    public boolean estaSatisfecho() { return satisfecho; }

    // ── Lógica de notificación (ruta normal del juego) ───────────────────────

    /**
     * Punto de entrada de la ruta NORMAL (gameplay): coloca el objeto y, si
     * cambia la condición de satisfacción, notifica a los observadores.
     */
    @Override
    public void setObjetoEncima(EntidadDinamica objeto) {
        super.setObjetoEncima(objeto);
        boolean ahora = esCajaValida(objeto);
        if (ahora != satisfecho) {
            satisfecho = ahora;
            notificar(ahora);
        }
    }

    private boolean esCajaValida(EntidadDinamica objeto) {
        return objeto != null && objeto.esObjetivoDeNivel();
    }

    private void notificar(boolean satisfecho) {
        for (ObservadorDestino observador : observadores) {
            if (satisfecho) observador.alSatisfacer();
            else            observador.alInsatisfacer();
        }
    }

    // ── Ruta silenciosa — solo usada por la restauración de Memento ──────────

    /**
     * Recalcula el flag {@code satisfecho} a partir del objeto actualmente
     * encima, sin notificar. Lo invoca {@link GestorDeVictoria#recontar()}.
     */
    void restaurarSatisfechoSilencioso() {
        satisfecho = esCajaValida(getObjetoEncima());
    }

    // ── Entidad ───────────────────────────────────────────────────────────────

    @Override
    public String getClaveImagen() { return "destino"; }

    @Override
    public void registrarEnTablero(Tablero tablero, int fila, int columna) {
        tablero.getGestorDeVictoria().registrarDestino(this);   
}
}
