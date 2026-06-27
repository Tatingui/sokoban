package modelo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Tablero — Originator del patrón GoF Memento.
 *
 * Responsabilidades de dominio:
 *  - Mantiene la grilla de entidades y la posición del jugador.
 *  - Expone el GestorDeCanales para el subsistema Observer.
 *
 * Responsabilidades de Memento:
 *  - {@link #guardarEstado(int)}   crea un TableroMemento con el estado actual.
 *  - {@link #restaurarEstado(TableroMemento)} aplica un snapshot de forma silenciosa,
 *    sin disparar notificaciones del Observer durante la restauración.
 */
public class Tablero {

    private Entidad[][] grilla;
    private int jugadorFila;
    private int jugadorColumna;
    private Sokoban jugador;
    private final GestorDeCanales  gestorDeCanales  = new GestorDeCanales();
    private final GestorDeVictoria gestorDeVictoria = new GestorDeVictoria();
    private final GestorDeCintas   gestorDeCintas   = new GestorDeCintas();
    private final GestorDePortales gestorDePortales = new GestorDePortales();

    // ── API de construcción (usada por ConstructorTablero) ───────────────────

    public void inicializarGrilla(int filas, int columnas) {
        grilla = new Entidad[filas][columnas];
    }

    public void colocarElemento(int fila, int columna, Entidad entidad) {
        grilla[fila][columna] = entidad;
    }

    public void setPosicionJugador(int fila, int columna) {
        this.jugadorFila    = fila;
        this.jugadorColumna = columna;
    }

    public void setJugador(Sokoban jugador) {
        this.jugador = jugador;
    }

    // ── Getters de estado ────────────────────────────────────────────────────

    public Sokoban getJugador()       { return jugador; }
    public int     getJugadorFila()   { return jugadorFila; }
    public int     getJugadorColumna(){ return jugadorColumna; }
    public int     getFilas()         { return grilla.length; }
    public int     getColumnas()      { return grilla[0].length; }
    public Entidad[][] getGrilla()    { return grilla; }
    public GestorDeCanales  getGestorDeCanales()  { return gestorDeCanales; }
    public GestorDeVictoria getGestorDeVictoria() { return gestorDeVictoria; }
    public GestorDeCintas   getGestorDeCintas()   { return gestorDeCintas; }
    public GestorDePortales getGestorDePortales() { return gestorDePortales; }

    public boolean esPosicionValida(int fila, int columna) {
        return fila >= 0 && fila < getFilas()
            && columna >= 0 && columna < getColumnas();
    }

    public Entidad getEntidad(int fila, int columna) {
        return grilla[fila][columna];
    }

    // ── Acceso y manipulación de dinámicas (usado por MotorMovimiento) ────────
    //
    // Estos métodos encapsulan la asimetría estructural del modelo de "casillero
    // intermedio": una EntidadDinamica puede estar directamente en la grilla o
    // como objetoEncima de un SueloEspecial. Centralizar esto aquí mantiene al
    // MotorMovimiento libre de instanceof.

    /** Entidad dinámica que ocupa la celda (jugador o caja), o null. */
    public EntidadDinamica getOcupante(int fila, int columna) {
        Entidad entidad = grilla[fila][columna];
        return entidad == null ? null : entidad.getOcupante();
    }

    /** Estrategia de suelo de la celda (patrón Strategy); {@link SueloFijo} si está vacía. */
    public EstrategiaDeSuelo estrategiaSueloEn(int fila, int columna) {
        Entidad entidad = grilla[fila][columna];
        return entidad == null ? SueloFijo.INSTANCIA : entidad.getEstrategiaDeSuelo();
    }

    /** True si el jugador puede pararse en la celda (no considera empuje). */
    public boolean esTransitableEn(int fila, int columna) {
        Entidad entidad = grilla[fila][columna];
        return entidad == null || entidad.esTransitable();
    }

    /** True si una caja puede quedar depositada en la celda (libre y transitable). */
    public boolean celdaLibreParaDinamica(int fila, int columna) {
        Entidad entidad = grilla[fila][columna];
        if (entidad == null) return true;
        return entidad.getOcupante() == null && entidad.esTransitable();
    }

    /** Deposita una dinámica: sobre la celda si aloja ocupantes, o reemplazándola en la grilla. */
    public void colocarDinamica(int fila, int columna, EntidadDinamica dinamica) {
        Entidad entidad = grilla[fila][columna];
        if (entidad.puedeAlojar()) {
            entidad.colocarOcupante(dinamica);
        } else {
            grilla[fila][columna] = dinamica;
        }
    }

    /** Retira la dinámica de la celda: deja intacta la celda que aloja, o repone SueloNormal. */
    public void quitarDinamica(int fila, int columna) {
        Entidad entidad = grilla[fila][columna];
        if (entidad.puedeAlojar()) {
            entidad.colocarOcupante(null);
        } else if (entidad.getOcupante() != null) {
            grilla[fila][columna] = new SueloNormal();
        }
    }

    /** Traslada la dinámica de una celda a otra respetando los suelos especiales. */
    public void moverDinamica(int filaOrigen, int columnaOrigen,
                              int filaDestino, int columnaDestino) {
        EntidadDinamica dinamica = getOcupante(filaOrigen, columnaOrigen);
        quitarDinamica(filaOrigen, columnaOrigen);
        colocarDinamica(filaDestino, columnaDestino, dinamica);
    }

    // ── Utilidades de consulta del estado del nivel ──────────────────────────

    /**
     * Cajas válidas actualmente sobre destinos. Lo mantiene el Observer
     * ({@link GestorDeVictoria}) en O(1); ya no se recorre la grilla.
     */
    public int getCajasEnDestino() {
        return gestorDeVictoria.getCajasEnDestino();
    }

    /** True cuando todos los destinos del nivel tienen una caja válida encima. */
    public boolean nivelResuelto() {
        return gestorDeVictoria.nivelResuelto();
    }

    /** Cerrojos actualmente activados (una llave válida encima). */
    public int contarCerrojosActivos() {
        return gestorDeCanales.contarCerrojosActivos();
    }

    // ── Memento: Originator — guardarEstado ──────────────────────────────────

    /**
     * Crea un snapshot inmutable del estado mutable actual.
     * El contadorMovimientos es propiedad del Caretaker; se inyecta aquí para
     * que el Memento sea autosuficiente (útil en save/load y tests).
     */
    public TableroMemento guardarEstado(int contadorMovimientos) {
        List<TableroMemento.SnapshotDinamico> dinamicos        = new ArrayList<>();
        Map<String, Object>                   estadosEstaticos = new HashMap<>();
        Map<String, Boolean>                  canales          = new HashMap<>();

        for (int f = 0; f < getFilas(); f++) {
            for (int c = 0; c < getColumnas(); c++) {
                Entidad entidad = grilla[f][c];
                if (entidad == null) continue;

                EntidadDinamica ocupante = entidad.getOcupante();

                // Dinámica presente (en la grilla o sobre un contenedor) + su estado interno.
                if (ocupante != null) {
                    boolean sobreContenedor = (ocupante != entidad);
                    dinamicos.add(new TableroMemento.SnapshotDinamico(
                            f, c, sobreContenedor, ocupante, ocupante.capturarEstadoMemento()));
                }

                // Estado de la celda estática (cerrojo activo, muro abierto…). Si la celda
                // ES la dinámica (ocupante == entidad), no hay celda estática que guardar.
                if (ocupante != entidad) {
                    Object estado = entidad.capturarEstadoMemento();
                    if (estado != null) estadosEstaticos.put(clave(f, c), estado);
                }
            }
        }

        // Estado de cada canal (no son celdas de la grilla)
        for (Canal canal : gestorDeCanales.getCanales()) {
            canales.put(canal.getId(), canal.estaAbierto());
        }

        Direccion mirada = jugador != null ? jugador.getMirada() : Direccion.ABAJO;
        return new TableroMemento(jugadorFila, jugadorColumna, mirada, contadorMovimientos,
                dinamicos, estadosEstaticos, canales);
    }

    // ── Memento: Originator — restaurarEstado ────────────────────────────────

    /**
     * Restaura el tablero al estado capturado en el memento. Todo es silencioso
     * (no se disparan notificaciones del Observer durante la restauración), así
     * que el orden entre celdas estáticas y canales no es crítico:
     *   1. Vaciar las dinámicas de la grilla.
     *   2. Reubicar cada dinámica y restaurar su estado interno (ej: resistencia).
     *   3. Restaurar el estado de las celdas estáticas (cerrojos, muros).
     *   4. Restaurar el estado de los canales.
     *   5. Actualizar coordenadas y mirada del jugador.
     *   6. Reconstruir el contador de victoria.
     */
    public void restaurarEstado(TableroMemento memento) {
        // 1 ──
        limpiarDinamicos();

        // 2 ── Reubicar dinámicas (ruta silenciosa) y restaurar su estado interno
        for (TableroMemento.SnapshotDinamico snap : memento.dinamicos) {
            if (snap.sobreContenedor) {
                grilla[snap.fila][snap.columna].colocarOcupanteSilencioso(snap.entidad);
            } else {
                grilla[snap.fila][snap.columna] = snap.entidad;
            }
            snap.entidad.aplicarEstadoMemento(snap.estado);
        }

        // 3 ── Estado de celdas estáticas (cerrojo activo, muro abierto…)
        for (Map.Entry<String, Object> entry : memento.estadosEstaticos.entrySet()) {
            int[] coord = parseClave(entry.getKey());
            grilla[coord[0]][coord[1]].aplicarEstadoMemento(entry.getValue());
        }

        // 4 ── Estado de los canales
        for (Map.Entry<String, Boolean> entry : memento.canales.entrySet()) {
            Canal canal = gestorDeCanales.obtener(entry.getKey());
            if (canal != null) canal.restaurarEstadoSilencioso(entry.getValue());
        }

        // 5 ── Coordenadas y mirada del jugador
        jugadorFila    = memento.jugadorFila;
        jugadorColumna = memento.jugadorColumna;
        if (jugador != null) jugador.setMirada(memento.miradaJugador);

        // 6 ── Reconstruir el contador de victoria (silencioso, sin notificar)
        gestorDeVictoria.recontar();
    }

    // ── Helpers privados ─────────────────────────────────────────────────────

    /**
     * Elimina todas las dinámicas de la grilla sin activar el Observer: las celdas
     * que alojan vacían su ocupante (ruta silenciosa) y las dinámicas directas
     * vuelven a ser SueloNormal.
     */
    private void limpiarDinamicos() {
        for (int f = 0; f < getFilas(); f++) {
            for (int c = 0; c < getColumnas(); c++) {
                Entidad entidad = grilla[f][c];
                if (entidad == null) continue;
                if (entidad.puedeAlojar()) {
                    entidad.colocarOcupanteSilencioso(null);
                } else if (entidad.getOcupante() != null) {
                    grilla[f][c] = new SueloNormal();
                }
            }
        }
    }

    private static String clave(int fila, int columna) {
        return fila + "," + columna;
    }

    private static int[] parseClave(String clave) {
        String[] p = clave.split(",");
        return new int[]{ Integer.parseInt(p[0]), Integer.parseInt(p[1]) };
    }
}
