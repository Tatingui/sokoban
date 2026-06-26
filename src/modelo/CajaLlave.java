// CajaLlave.java
package modelo;

/**
 * Caja llave. Puede ser de dos tipos:
 *  - de un canal concreto (ej. AZUL): solo activa cerrojos de ese canal;
 *  - comodín (multicanal): activa CUALQUIER cerrojo, sin importar el canal. Su
 *    sprite combina todos los colores, evitando tener que dibujar una imagen por
 *    cada combinación posible de canales.
 *
 * La distinción entre llaves normales y comodín se hace por fábrica estática
 * ({@link #deCanal(String)} / {@link #comodin()}) en lugar de un constructor
 * público, para que el tipo de llave quede explícito en el punto de creación.
 */
public class CajaLlave extends Caja {

    private final String  canal;    // null cuando es comodín
    private final boolean comodin;

    private CajaLlave(String canal, boolean comodin) {
        this.canal   = canal;
        this.comodin = comodin;
    }

    /** Llave de un único canal (ej. AZUL, VERDE, NARANJA). */
    public static CajaLlave deCanal(String canal) {
        return new CajaLlave(canal, false);
    }

    /** Llave comodín: abre cualquier cerrojo, sin importar el canal. */
    public static CajaLlave comodin() {
        return new CajaLlave(null, true);
    }

    /** Un comodín pertenece a todos los canales; una llave normal, solo al suyo. */
    public boolean perteneceACanal(String idCanal) {
        return comodin || idCanal.equals(canal);
    }

    public boolean esComodin() { return comodin; }

    /** Una llave es una herramienta, no una caja-objetivo: no cuenta en los destinos. */
    @Override
    public boolean esObjetivoDeNivel() { return false; }

    /**
     * Clave de sprite:
     *  - comodín → "llave_MULTICANAL" (sprite con todos los colores);
     *  - normal  → "llave_AZUL" (o NARANJA, VERDE …).
     */
    @Override
    public String getClaveImagen() {
        return comodin ? "llave_MULTICANAL" : "llave_" + canal;
    }
}
