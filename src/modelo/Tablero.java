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

    /** Deposita una dinámica: sobre el contenedor (suelo especial o muro) si lo hay, o en la grilla. */
    public void colocarDinamica(int fila, int columna, EntidadDinamica dinamica) {
        Entidad entidad = grilla[fila][columna];
        if (entidad instanceof EntidadEstaticaConOcupante contenedor) {
            contenedor.setObjetoEncima(dinamica);
        } else {
            grilla[fila][columna] = dinamica;
        }
    }

    /** Retira la dinámica de la celda: deja intacto el contenedor o repone SueloNormal. */
    public void quitarDinamica(int fila, int columna) {
        Entidad entidad = grilla[fila][columna];
        if (entidad instanceof EntidadEstaticaConOcupante contenedor) {
            contenedor.setObjetoEncima(null);
        } else if (entidad instanceof EntidadDinamica) {
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

    /** Cuenta las cajas (de cualquier tipo) presentes en la grilla. */
    public int contarCajas() {
        int total = 0;
        for (int f = 0; f < getFilas(); f++) {
            for (int c = 0; c < getColumnas(); c++) {
                Entidad e = grilla[f][c];
                if (e instanceof Caja) {
                    total++;
                } else if (e instanceof SueloEspecial suelo
                        && suelo.getObjetoEncima() instanceof Caja) {
                    total++;
                }
            }
        }
        return total;
    }

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

    // ── Memento: Originator — guardarEstado ──────────────────────────────────

    /**
     * Crea un snapshot inmutable del estado mutable actual.
     * El contadorMovimientos es propiedad del Caretaker; se inyecta aquí para
     * que el Memento sea autosuficiente (útil en save/load y tests).
     */
    public TableroMemento guardarEstado(int contadorMovimientos) {
        List<TableroMemento.SnapshotDinamico>         dinamicos = new ArrayList<>();
        Map<String, TableroMemento.SnapshotCerrojo>   cerrojos  = new HashMap<>();
        Map<String, TableroMemento.SnapshotCanal>     canales   = new HashMap<>();
        Map<String, Boolean>                          muros     = new HashMap<>();

        for (int f = 0; f < getFilas(); f++) {
            for (int c = 0; c < getColumnas(); c++) {
                Entidad entidad = grilla[f][c];

                // Dinámica directa en la grilla (modelo de reemplazo: bajo ella hay SueloNormal)
                if (entidad instanceof EntidadDinamica dinamica) {
                    int res = dinamica instanceof CajaFragil cf ? cf.getResistencia() : -1;
                    dinamicos.add(new TableroMemento.SnapshotDinamico(f, c, false, dinamica, res));

                } else {
                    // Objeto encima de un contenedor (suelo especial o muro abierto)
                    if (entidad instanceof EntidadEstaticaConOcupante contenedor && contenedor.estaOcupado()) {
                        EntidadDinamica obj = contenedor.getObjetoEncima();
                        int res = obj instanceof CajaFragil cf ? cf.getResistencia() : -1;
                        dinamicos.add(new TableroMemento.SnapshotDinamico(f, c, true, obj, res));
                    }

                    // Estado interno del cerrojo
                    if (entidad instanceof CasilleroCerrojo cerrojo) {
                        cerrojos.put(clave(f, c),
                                new TableroMemento.SnapshotCerrojo(cerrojo.estaActivo()));
                    }

                    // Estado del muro (abierto/cerrado); puede además tener un ocupante encima
                    if (entidad instanceof Muro muro) {
                        muros.put(clave(f, c), muro.estaAbierto());
                    }
                }
            }
        }

        // Estado de cada canal
        for (Canal canal : gestorDeCanales.getCanales()) {
            canales.put(canal.getId(),
                    new TableroMemento.SnapshotCanal(canal.estaAbierto()));
        }

        Direccion mirada = jugador != null ? jugador.getMirada() : Direccion.ABAJO;
        return new TableroMemento(jugadorFila, jugadorColumna, mirada, contadorMovimientos,
                dinamicos, cerrojos, canales, muros);
    }

    // ── Memento: Originator — restaurarEstado ────────────────────────────────

    /**
     * Restaura el tablero al estado capturado en el memento.
     *
     * Orden crítico para no disparar el Observer durante la restauración:
     *   1. Limpiar dinámicas de la grilla (silencioso en CasilleroCerrojo).
     *   2. Recolocar dinámicas en sus posiciones del snapshot (silencioso).
     *   3. Restaurar resistencia de CajaFragil (valor puro, sin side-effects).
     *   4. Restaurar estado interno de cada cerrojo (silencioso).
     *   5. Restaurar estado de cada canal (silencioso, sin evaluarEstado).
     *   6. Restaurar estado de cada muro (silencioso, sin notificar).
     *   7. Actualizar coordenadas del jugador.
     *   8. Reconstruir el contador de victoria (silencioso, sin notificar).
     */
    public void restaurarEstado(TableroMemento memento) {
        // 1 ── Vaciar dinámicas sin activar Observer
        limpiarDinamicos();

        // 2 ── Reubicar dinámicas (ruta silenciosa unificada para todo contenedor)
        for (TableroMemento.SnapshotDinamico snap : memento.dinamicos) {
            if (snap.sobreContenedor) {
                EntidadEstaticaConOcupante contenedor =
                        (EntidadEstaticaConOcupante) grilla[snap.fila][snap.columna];
                contenedor.setObjetoEncimaSilencioso(snap.entidad);
            } else {
                grilla[snap.fila][snap.columna] = snap.entidad;
            }
        }

        // 3 ── Restaurar resistencia de cajas frágiles
        for (TableroMemento.SnapshotDinamico snap : memento.dinamicos) {
            if (snap.entidad instanceof CajaFragil cf && snap.resistencia >= 0) {
                cf.setResistencia(snap.resistencia);
            }
        }

        // 4 ── Restaurar estado interno de cerrojos (silencioso)
        for (Map.Entry<String, TableroMemento.SnapshotCerrojo> entry : memento.cerrojos.entrySet()) {
            int[] coord = parseClave(entry.getKey());
            CasilleroCerrojo cerrojo = (CasilleroCerrojo) grilla[coord[0]][coord[1]];
            cerrojo.restaurarEstadoSilencioso(entry.getValue().activo);
        }

        // 5 ── Restaurar estado de canales (silencioso — sin llamar evaluarEstado)
        for (Map.Entry<String, TableroMemento.SnapshotCanal> entry : memento.canales.entrySet()) {
            Canal canal = gestorDeCanales.obtener(entry.getKey());
            if (canal != null) {
                canal.restaurarEstadoSilencioso(entry.getValue().abierto);
            }
        }

        // 6 ── Restaurar estado de muros (silencioso — sin notificar Observer)
        for (Map.Entry<String, Boolean> entry : memento.muros.entrySet()) {
            int[] coord = parseClave(entry.getKey());
            ((Muro) grilla[coord[0]][coord[1]]).restaurarEstadoSilencioso(entry.getValue());
        }

        // 7 ── Actualizar coordenadas y mirada del jugador
        jugadorFila    = memento.jugadorFila;
        jugadorColumna = memento.jugadorColumna;
        if (jugador != null) jugador.setMirada(memento.miradaJugador);

        // 8 ── Reconstruir el contador de victoria (silencioso, sin notificar)
        gestorDeVictoria.recontar();
    }

    // ── Helpers privados ─────────────────────────────────────────────────────

    /**
     * Elimina todas las EntidadDinamica de la grilla sin activar el Observer.
     * Las celdas que contenían una dinámica directa vuelven a SueloNormal.
     * Las SueloEspecial con objetoEncima quedan vacías mediante la ruta silenciosa.
     */
    private void limpiarDinamicos() {
        for (int f = 0; f < getFilas(); f++) {
            for (int c = 0; c < getColumnas(); c++) {
                Entidad entidad = grilla[f][c];
                if (entidad instanceof EntidadDinamica) {
                    grilla[f][c] = new SueloNormal();
                } else if (entidad instanceof EntidadEstaticaConOcupante contenedor
                        && contenedor.estaOcupado()) {
                    contenedor.setObjetoEncimaSilencioso(null);
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
