package modelo;

/**
 * Muro gobernado por un canal de cerrojos. Rol Concrete Observer del patron
 * Observer: el {@link Canal} lo notifica para que mute su propio estado entre
 * cerrado (infranqueable) y abierto (transitable), sin necesidad de
 * reemplazar la entidad en la grilla.
 *
 * Unifica lo que antes eran dos clases casi identicas (MuroCerrado /
 * MuroAbierto), eliminando la duplicacion: el estado se modela con un boolean.
 */
public class Muro extends EntidadEstatica implements ObservadorCanal {

    private final String idCanal;
    private boolean abierto;

    public Muro(String idCanal) {
        this(idCanal, false); // por defecto el muro nace cerrado
    }

    public Muro(String idCanal, boolean abierto) {
        this.idCanal = idCanal;
        this.abierto = abierto;
    }

    public String getIdCanal() {
        return idCanal;
    }

    public boolean estaAbierto() {
        return abierto;
    }

    @Override
    public void alAbrirCanal() {
        abierto = true;
    }

    @Override
    public void alCerrarCanal() {
        abierto = false;
    }

    @Override
    public boolean esTransitable() {
        return abierto;
    }

    @Override
    public String getClaveSprite() {
        String prefijo = estaAbierto() ? "muroAbierto_" : "muroCerrado_";
        return prefijo + idCanal;
    }
}
