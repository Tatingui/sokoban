package modelo;

/**
 * Entidad estática que puede tener una {@link EntidadDinamica} encima sin perder
 * su identidad (modelo de "casillero intermedio" descrito en la documentación).
 *
 * La comparten dos familias de celdas transitables que deben persistir cuando
 * algo se les para encima:
 *  - los suelos especiales ({@link SueloEspecial}: Destino, Cerrojo, Resbaladizo);
 *  - el {@link Muro} abierto (una puerta transitable que NO debe desaparecer al
 *    ser cruzada).
 *
 * Cuando el jugador o una caja entran a la celda, el objeto se guarda en
 * {@code objetoEncima} y la entidad permanece en la grilla; al retirarse, el
 * suelo o la puerta reaparecen intactos.
 */
public abstract class EntidadEstaticaConOcupante extends EntidadEstatica {

    private EntidadDinamica objetoEncima;

    public EntidadDinamica getObjetoEncima() { return objetoEncima; }
    public void setObjetoEncima(EntidadDinamica objeto) { this.objetoEncima = objeto; }
    public boolean estaOcupado() { return objetoEncima != null; }

    /** El ocupante de la celda es el objeto que tiene encima (o null). */
    @Override
    public EntidadDinamica getOcupante() {
        return objetoEncima;
    }

    /** Estas celdas sí alojan una dinámica encima (modelo de casillero intermedio). */
    @Override
    public boolean puedeAlojar() {
        return true;
    }

    /** Ruta de gameplay: delega en {@link #setObjetoEncima} (que las subclases sobreescriben). */
    @Override
    public void colocarOcupante(EntidadDinamica dinamica) {
        setObjetoEncima(dinamica);
    }

    /** Ruta de Memento: asigna sin disparar la lógica de las subclases. */
    @Override
    public void colocarOcupanteSilencioso(EntidadDinamica dinamica) {
        this.objetoEncima = dinamica;
    }
}
