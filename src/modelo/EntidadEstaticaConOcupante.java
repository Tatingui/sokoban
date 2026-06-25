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

    /**
     * Ruta silenciosa común para el Memento: asigna el objeto encima escribiendo
     * el campo directamente, sin pasar por las sobreescrituras de gameplay de las
     * subclases (activación de cerrojos, notificación de destinos, etc.).
     * Acceso package-private: solo {@link Tablero} la usa durante la restauración.
     */
    void setObjetoEncimaSilencioso(EntidadDinamica objeto) {
        this.objetoEncima = objeto;
    }
}
