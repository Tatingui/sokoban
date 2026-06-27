// Sokoban.java
package modelo;

public class Sokoban extends EntidadDinamica {

    /** Dirección hacia la que mira el jugador (define su sprite y el disparo de portales). */
    private Direccion mirada = Direccion.ABAJO;

    public Direccion getMirada() { return mirada; }
    public void setMirada(Direccion mirada) { this.mirada = mirada; }

    /** Override to identify the player entity without instanceof checks. */
    @Override
    public boolean esJugador() { return true; }

    /** El jugador no es empujable: distingue al Sokoban de las cajas sin instanceof. */
    @Override
    public boolean esEmpujable() { return false; }

    @Override
    public String getClaveImagen() {
        return switch (mirada) {
            case ARRIBA    -> "sokobanEspalda";
            case ABAJO     -> "sokobanFrente";
            case IZQUIERDA -> "sokobanIzquierda";
            case DERECHA   -> "sokobanDerecha";
        };
    }

    @Override
    public void registrarEnTablero(Tablero tablero, int fila, int columna) {
        tablero.setPosicionJugador(fila, columna);
        tablero.setJugador(this);
}
}

