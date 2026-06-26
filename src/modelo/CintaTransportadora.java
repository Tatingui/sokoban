package modelo;

/**
 * Piso de cinta transportadora: cada vez que el jugador hace un movimiento
 * válido, arrastra lo que tenga encima una celda en su dirección. Ese efecto lo
 * aporta la estrategia {@link ArrastreCinta} (patrón Strategy), disparada por el
 * {@link GestorDeCintas} al cerrarse el turno.
 *
 * Hereda de {@link SueloEspecial}, así que persiste bajo el objeto que arrastra.
 */
public class CintaTransportadora extends SueloEspecial {

    private final Direccion direccion;
    private final EstrategiaDeSuelo estrategia;

    public CintaTransportadora(Direccion direccion) {
        this.direccion  = direccion;
        this.estrategia = new ArrastreCinta(direccion);
    }

    public Direccion getDireccion() { return direccion; }

    @Override
    public EstrategiaDeSuelo getEstrategiaDeSuelo() {
        return estrategia;
    }

    @Override
    public String getClaveImagen() {
        return "cinta_" + direccion;   // ej: cinta_ARRIBA, cinta_DERECHA
    }
}
