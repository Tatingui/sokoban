package modelo;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Centraliza los canales del tablero (lo que la documentacion describe como los
 * mapas {@code cerrojosPorCanal} / {@code murosPorCanal}). El {@link Tablero} lo
 * expone y el constructor del tablero registra en el cada cerrojo y cada muro.
 *
 * Tambien coordina el modo exclusivo: cuando una llave multicanal activa un
 * cerrojo exclusivo, suprime los demas canales de esa llave.
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

    /** Asocia un cerrojo a su canal y le da acceso al gestor para el modo exclusivo. */
    public void registrarCerrojo(CasilleroCerrojo cerrojo) {
        Canal canal = obtenerOCrear(cerrojo.getIdCanal());
        canal.registrarCerrojo(cerrojo);
        cerrojo.asociarGestor(this, canal);
    }

    /** Suscribe un muro como observador de su canal. */
    public void registrarMuro(Muro muro) {
        obtenerOCrear(muro.getIdCanal()).suscribir(muro);
    }

    /** Cierra todos los canales de la llave distintos del que la activo (cerrojo exclusivo). */
    public void suprimirOtrosCanales(CajaLlave llave, String canalActivo) {
        aplicarSupresion(llave, canalActivo, true);
    }

    /** Libera la supresion al retirar la llave del cerrojo exclusivo. */
    public void liberarOtrosCanales(CajaLlave llave, String canalActivo) {
        aplicarSupresion(llave, canalActivo, false);
    }

    private void aplicarSupresion(CajaLlave llave, String canalActivo, boolean suprimir) {
        for (String idCanal : llave.getCanales()) {
            if (!idCanal.equals(canalActivo)) {
                Canal canal = canales.get(idCanal);
                if (canal != null) {
                    canal.setSuprimido(suprimir);
                }
            }
        }
    }
}
