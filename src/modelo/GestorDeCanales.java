package modelo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Centraliza los canales del tablero (lo que la documentacion describe como los
 * mapas {@code cerrojosPorCanal} / {@code murosPorCanal}). El {@link Tablero} lo
 * expone y el constructor del tablero registra en el cada cerrojo y cada muro.
 */
public class GestorDeCanales {

    private final Map<String, Canal> canales = new HashMap<>();

    public Canal obtenerOCrear(String idCanal) {
        return canales.computeIfAbsent(idCanal, Canal::new);
    }

    public Canal obtener(String idCanal) {
        return canales.get(idCanal);
    }

    public Collection<Canal> getCanales() {
        return canales.values();
    }

    /** Total de cerrojos activados en todos los canales. */
    public int contarCerrojosActivos() {
        int activos = 0;
        for (Canal canal : canales.values()) {
            activos += canal.contarCerrojosActivos();
        }
        return activos;
    }

    /** Asocia un cerrojo a su canal (cableado del patrón Observer). */
    public void registrarCerrojo(CasilleroCerrojo cerrojo) {
        Canal canal = obtenerOCrear(cerrojo.getIdCanal());
        canal.registrarCerrojo(cerrojo);
        cerrojo.asociarCanal(canal);
    }

    /** Suscribe un muro como observador de su canal. */
    public void registrarMuro(Muro muro) {
        obtenerOCrear(muro.getIdCanal()).suscribir(muro);
    }
}
