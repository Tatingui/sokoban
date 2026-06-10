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
    private final GestorDeCanales gestorDeCanales = new GestorDeCanales();

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
    public GestorDeCanales getGestorDeCanales() { return gestorDeCanales; }

    public boolean esPosicionValida(int fila, int columna) {
        return fila >= 0 && fila < getFilas()
            && columna >= 0 && columna < getColumnas();
    }

    public Entidad getEntidad(int fila, int columna) {
        return grilla[fila][columna];
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

    /** Cuenta las cajas que ya reposan sobre una casilla Destino. */
    public int contarCajasEnDestino() {
        int total = 0;
        for (int f = 0; f < getFilas(); f++) {
            for (int c = 0; c < getColumnas(); c++) {
                if (grilla[f][c] instanceof Destino destino
                        && destino.getObjetoEncima() instanceof Caja) {
                    total++;
                }
            }
        }
        return total;
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

                // Dinámicas directas en la grilla (sin SueloEspecial debajo)
                if (entidad instanceof EntidadDinamica dinamica) {
                    int res = dinamica instanceof CajaFragil cf ? cf.getResistencia() : -1;
                    dinamicos.add(new TableroMemento.SnapshotDinamico(f, c, false, dinamica, res));

                } else if (entidad instanceof SueloEspecial suelo) {

                    // Dinámica encima de un suelo especial (Destino, Cerrojo, Resbaladizo)
                    if (suelo.estaOcupado()) {
                        EntidadDinamica obj = suelo.getObjetoEncima();
                        int res = obj instanceof CajaFragil cf ? cf.getResistencia() : -1;
                        dinamicos.add(new TableroMemento.SnapshotDinamico(f, c, true, obj, res));
                    }

                    // Estado interno del cerrojo
                    if (entidad instanceof CasilleroCerrojo cerrojo) {
                        String coordLlave = cerrojo.getLlaveActiva() != null
                                ? encontrarCoordenada(cerrojo.getLlaveActiva())
                                : null;
                        cerrojos.put(clave(f, c),
                                new TableroMemento.SnapshotCerrojo(cerrojo.estaActivo(), coordLlave));
                    }

                } else if (entidad instanceof Muro muro) {
                    muros.put(clave(f, c), muro.estaAbierto());
                }
            }
        }

        // Estado de cada canal
        for (Canal canal : gestorDeCanales.getCanales()) {
            canales.put(canal.getId(),
                    new TableroMemento.SnapshotCanal(canal.estaAbierto(), canal.isSuprimido()));
        }

        return new TableroMemento(jugadorFila, jugadorColumna, contadorMovimientos,
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
     */
    public void restaurarEstado(TableroMemento memento) {
        // 1 ── Vaciar dinámicas sin activar Observer
        limpiarDinamicos();

        // 2 ── Reubicar dinámicas (ruta silenciosa para CasilleroCerrojo)
        for (TableroMemento.SnapshotDinamico snap : memento.dinamicos) {
            if (snap.sobreSueloEspecial) {
                SueloEspecial suelo = (SueloEspecial) grilla[snap.fila][snap.columna];
                if (suelo instanceof CasilleroCerrojo cerrojo) {
                    cerrojo.setObjetoEncimaSilencioso(snap.entidad);
                } else {
                    suelo.setObjetoEncima(snap.entidad);
                }
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
            TableroMemento.SnapshotCerrojo snap = entry.getValue();
            CajaLlave llaveActiva = resolverLlave(snap.coordLlaveActiva);
            cerrojo.restaurarEstadoSilencioso(snap.activo, llaveActiva);
        }

        // 5 ── Restaurar estado de canales (silencioso — sin llamar evaluarEstado)
        for (Map.Entry<String, TableroMemento.SnapshotCanal> entry : memento.canales.entrySet()) {
            Canal canal = gestorDeCanales.obtener(entry.getKey());
            if (canal != null) {
                canal.restaurarEstadoSilencioso(entry.getValue().abierto,
                                                entry.getValue().suprimido);
            }
        }

        // 6 ── Restaurar estado de muros (silencioso — sin notificar Observer)
        for (Map.Entry<String, Boolean> entry : memento.muros.entrySet()) {
            int[] coord = parseClave(entry.getKey());
            ((Muro) grilla[coord[0]][coord[1]]).restaurarEstadoSilencioso(entry.getValue());
        }

        // 7 ── Actualizar coordenadas del jugador
        jugadorFila    = memento.jugadorFila;
        jugadorColumna = memento.jugadorColumna;
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
                } else if (entidad instanceof SueloEspecial suelo && suelo.estaOcupado()) {
                    if (suelo instanceof CasilleroCerrojo cerrojo) {
                        cerrojo.setObjetoEncimaSilencioso(null);
                    } else {
                        suelo.setObjetoEncima(null);
                    }
                }
            }
        }
    }

    /**
     * Busca una EntidadDinamica en la grilla y devuelve su clave de coordenada,
     * tanto si está directamente en una celda como si es el objetoEncima de un suelo.
     */
    private String encontrarCoordenada(EntidadDinamica entidad) {
        for (int f = 0; f < getFilas(); f++) {
            for (int c = 0; c < getColumnas(); c++) {
                if (grilla[f][c] == entidad) return clave(f, c);
                if (grilla[f][c] instanceof SueloEspecial suelo
                        && suelo.getObjetoEncima() == entidad) return clave(f, c);
            }
        }
        return null;
    }

    /**
     * Resuelve la coordenada de una CajaLlave almacenada en el snapshot.
     * Devuelve null si la coordenada es null o la celda no contiene una CajaLlave.
     */
    private CajaLlave resolverLlave(String coord) {
        if (coord == null) return null;
        int[] pos = parseClave(coord);
        Entidad entidad = grilla[pos[0]][pos[1]];
        if (entidad instanceof CajaLlave ck) return ck;
        if (entidad instanceof SueloEspecial suelo
                && suelo.getObjetoEncima() instanceof CajaLlave ck) return ck;
        return null;
    }

    private static String clave(int fila, int columna) {
        return fila + "," + columna;
    }

    private static int[] parseClave(String clave) {
        String[] p = clave.split(",");
        return new int[]{ Integer.parseInt(p[0]), Integer.parseInt(p[1]) };
    }
}
