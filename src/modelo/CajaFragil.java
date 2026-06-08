// CajaFragil.java
package modelo;

public class CajaFragil extends Caja {
    private int resistencia;

    public CajaFragil(int resistencia) {
        this.resistencia = resistencia;
    }

    public void reducirResistencia() { this.resistencia--; }
    public boolean estaRota() { return resistencia <= 0; }
    public int getResistencia() { return resistencia; }

    @Override
    public String getClaveSprite() {
        return "cajaFragil";
    }
}

