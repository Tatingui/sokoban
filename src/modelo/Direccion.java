package modelo;

public enum Direccion {
    ARRIBA(-1, 0),
    ABAJO(1, 0),
    IZQUIERDA(0, -1),
    DERECHA(0, 1);

    private final int deltaFila;
    private final int deltaColumna;

    Direccion(int deltaFila, int deltaColumna) {
        this.deltaFila = deltaFila;
        this.deltaColumna = deltaColumna;
    }

    public int getDeltaFila() {
        return deltaFila;
    }

    public int getDeltaColumna() {
        return deltaColumna;
    }

    /** Dirección contraria (ARRIBA↔ABAJO, IZQUIERDA↔DERECHA). */
    public Direccion getOpuesta() {
        return switch (this) {
            case ARRIBA    -> ABAJO;
            case ABAJO     -> ARRIBA;
            case IZQUIERDA -> DERECHA;
            case DERECHA   -> IZQUIERDA;
        };
    }
}
