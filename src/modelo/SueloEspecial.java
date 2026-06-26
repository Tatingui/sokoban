package modelo;

/**
 * Suelo especial: una celda transitable que puede albergar un objeto encima
 * (Destino, CasilleroCerrojo, SueloResbaladizo). Mientras no esté ocupado se
 * puede pisar; ocupado, bloquea el paso. La mecánica de "objeto encima" la
 * provee {@link EntidadEstaticaConOcupante}.
 */
public abstract class SueloEspecial extends EntidadEstaticaConOcupante {

    @Override
    public boolean esTransitable() {
        return !estaOcupado();
    }
}
