// CasilleroCerrojo.java
package modelo;

/**
 * Casillero cerrojo. Hereda de {@link SueloEspecial} (puede tener un objeto
 * encima) y participa del patron Observer como parte del Subject: reporta a su
 * {@link Canal} cada cambio en su estado de activacion.
 *
 * Se activa cuando la {@link CajaLlave} colocada encima pertenece a su canal.
 * Si ademas es exclusivo, al activarse suprime los otros canales de esa llave.
 */
public class CasilleroCerrojo extends SueloEspecial {

    private final String idCanal;
    private final boolean modoExclusivo;
    private boolean activo;

    private Canal canal;
    private GestorDeCanales gestor;
    /** Llave que activo el cerrojo; se recuerda para liberar la supresion al retirarla. */
    private CajaLlave llaveActiva;

    public CasilleroCerrojo(String idCanal, boolean modoExclusivo) {
        this.idCanal = idCanal;
        this.modoExclusivo = modoExclusivo;
    }

    public String getIdCanal() {
        return idCanal;
    }

    public boolean isModoExclusivo() {
        return modoExclusivo;
    }

    public boolean estaActivo() {
        return activo;
    }

    /** El gestor de canales se asocia durante la construccion del tablero. */
    public void asociarGestor(GestorDeCanales gestor, Canal canal) {
        this.gestor = gestor;
        this.canal = canal;
    }

    @Override
    public void setObjetoEncima(EntidadDinamica objeto) {
        super.setObjetoEncima(objeto);
        if (esLlaveDelCanal(objeto)) {
            activar((CajaLlave) objeto);
        } else {
            desactivar();
        }
    }

    private boolean esLlaveDelCanal(EntidadDinamica objeto) {
        return objeto instanceof CajaLlave
                && ((CajaLlave) objeto).perteneceACanal(idCanal);
    }

    private void activar(CajaLlave llave) {
        if (activo) {
            return;
        }
        activo = true;
        llaveActiva = llave;
        if (modoExclusivo && gestor != null) {
            gestor.suprimirOtrosCanales(llave, idCanal);
        }
        notificarCanal();
    }

    private void desactivar() {
        if (!activo) {
            return;
        }
        activo = false;
        if (modoExclusivo && gestor != null && llaveActiva != null) {
            gestor.liberarOtrosCanales(llaveActiva, idCanal);
        }
        llaveActiva = null;
        notificarCanal();
    }

    private void notificarCanal() {
        if (canal != null) {
            canal.evaluarEstado();
        }
    }

    @Override
    public String getClaveImagen() { return "cerrojo_" + idCanal; }
}
