package modelo;

/**
 * Rol Observer del patrón Observer aplicado a la condición de victoria.
 * Lo implementan los objetos interesados en saber cuándo un {@link Destino}
 * pasa a estar satisfecho (tiene una caja válida encima) o deja de estarlo.
 * El {@link GestorDeVictoria} es el observador concreto que cuenta los destinos
 * satisfechos para decidir si el nivel está resuelto.
 */
public interface ObservadorDestino {
    /** El destino pasó a tener una caja válida encima. */
    void alSatisfacer();

    /** El destino dejó de tener una caja válida encima. */
    void alInsatisfacer();
}
