package modelo;

/**
 * Subsistema de los portales. Mantiene dos "slots" (azul y naranja); disparar un
 * color reposiciona ese slot, de modo que siempre hay como máximo uno de cada.
 *
 * Disparar es un ray-cast: el proyectil parte de Sokoban en su mirada y avanza
 * atravesando cajas, puertas y suelos, deteniéndose SOLO contra una pared
 * ({@link Entidad#detienePortal()}). El portal queda en la última celda libre
 * antes de la pared.
 */
public class GestorDePortales {

    private Portal azul;
    private Portal naranja;

    public Portal getAzul()    { return azul; }
    public Portal getNaranja() { return naranja; }

    // ── Disparo ───────────────────────────────────────────────────────────────

    /**
     * Dispara (reposiciona) el portal del color dado. Devuelve {@code true} si se
     * colocó. Es inválido (no hace nada) si caería en la misma arista que el otro
     * portal: no se permiten dos portales en la misma pared.
     */
    public boolean disparar(Tablero tablero, ColorPortal color,
                            int filaOrigen, int columnaOrigen, Direccion direccion) {
        Portal nuevo = trazar(tablero, filaOrigen, columnaOrigen, direccion);
        if (nuevo == null) return false;

        Portal otro = (color == ColorPortal.AZUL) ? naranja : azul;
        if (nuevo.equals(otro)) return false;   // misma arista que el otro portal → inválido

        if (color == ColorPortal.AZUL) azul = nuevo;
        else                           naranja = nuevo;
        return true;
    }

    /** Avanza hasta la pared más cercana y devuelve el portal en la celda previa. */
    private Portal trazar(Tablero tablero, int fila, int columna, Direccion direccion) {
        int f = fila, c = columna;
        while (true) {
            int nf = f + direccion.getDeltaFila();
            int nc = c + direccion.getDeltaColumna();
            if (!tablero.esPosicionValida(nf, nc)) return null;     // no debería pasar (borde = pared)
            Entidad celda = tablero.getEntidad(nf, nc);
            if (celda != null && celda.detienePortal()) {
                return new Portal(f, c, direccion);                 // última celda antes de la pared
            }
            f = nf;  c = nc;                                        // atraviesa cajas, puertas, suelos
        }
    }

    // ── Teletransporte ────────────────────────────────────────────────────────

    /**
     * Si en (fila, columna) hay un portal cuyo lado es {@code direccion} y existe
     * su par, devuelve el portal de salida; en caso contrario, {@code null}.
     */
    public Portal salidaDesde(int fila, int columna, Direccion direccion) {
        if (esEntrada(azul, fila, columna, direccion))    return naranja;
        if (esEntrada(naranja, fila, columna, direccion)) return azul;
        return null;
    }

    private boolean esEntrada(Portal portal, int fila, int columna, Direccion direccion) {
        return portal != null
                && portal.fila() == fila
                && portal.columna() == columna
                && portal.lado() == direccion;
    }
}
