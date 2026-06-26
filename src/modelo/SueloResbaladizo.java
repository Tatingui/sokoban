// SueloResbaladizo.java
package modelo;

/**
 * Piso resbaladizo: una entidad que llega aquí sigue deslizándose en su
 * dirección hasta abandonar el hielo o chocar contra algo sólido. Ese
 * comportamiento lo aporta la estrategia {@link Deslizamiento} (patrón Strategy).
 */
public class SueloResbaladizo extends SueloEspecial {

    @Override
    public EstrategiaDeSuelo getEstrategiaDeSuelo() {
        return Deslizamiento.INSTANCIA;
    }

    @Override
    public String getClaveImagen() { return "sueloResbaladizo"; }
}
