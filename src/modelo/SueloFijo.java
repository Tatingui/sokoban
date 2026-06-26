package modelo;

/**
 * Estrategia de suelo por defecto (Concrete Strategy): no produce ningún efecto,
 * ni al llegar ni al resolverse el turno. La usan el suelo normal y, por
 * herencia, cualquier celda que no defina un comportamiento especial.
 *
 * Es stateless, así que se comparte una única instancia (se apoya en los métodos
 * default de {@link EstrategiaDeSuelo}).
 */
public final class SueloFijo implements EstrategiaDeSuelo {

    public static final SueloFijo INSTANCIA = new SueloFijo();

    private SueloFijo() { }
}
