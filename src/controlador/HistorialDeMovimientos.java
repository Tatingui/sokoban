package controlador;

import modelo.TableroMemento;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * HistorialDeMovimientos — Caretaker del patrón GoF Memento.
 *
 * Única responsable de almacenar y descartar snapshots ({@link TableroMemento}).
 * No conoce al Originator ({@code modelo.Tablero}) más allá del tipo opaco del
 * memento: no lo crea ni lo aplica, solo lo apila y lo entrega de vuelta.
 *
 *  - {@link #apilar(TableroMemento)}: guarda un snapshot, respetando
 *    {@link #LIMITE_HISTORIAL} (descarta el más antiguo si se supera).
 *  - {@link #retroceder()}: desapila {@link #PASOS_POR_RETROCESO} snapshots de una
 *    sola vez y devuelve el más antiguo de ellos (el estado al que hay que volver).
 *    Devuelve {@code null} si hay menos de {@link #PASOS_POR_RETROCESO} snapshots
 *    (no hay retroceso parcial).
 */
public class HistorialDeMovimientos {

    /** Máximo de movimientos que se pueden deshacer (snapshots guardados). */
    public static final int LIMITE_HISTORIAL = 15;

    /** Pasos que retrocede cada pulsación del botón de undo. */
    public static final int PASOS_POR_RETROCESO = 5;

    /** Pila de snapshots: el tope es el estado ANTES del último movimiento. */
    private final Deque<TableroMemento> snapshots = new ArrayDeque<>();

    /**
     * Apila un snapshot, respetando el límite de historial.
     * Si se supera el límite se elimina el snapshot más antiguo (FIFO del fondo).
     */
    public void apilar(TableroMemento snapshot) {
        if (snapshots.size() >= LIMITE_HISTORIAL) {
            snapshots.pollLast();  // descarta el más antiguo
        }
        snapshots.push(snapshot);  // apila al frente (más reciente)
    }

    /**
     * Retrocede {@link #PASOS_POR_RETROCESO} pasos de una sola vez: desapila esa
     * cantidad de snapshots y devuelve el más antiguo de ellos. No hace nada (y
     * devuelve {@code null}) si quedan menos de {@link #PASOS_POR_RETROCESO}
     * snapshots en el historial.
     */
    public TableroMemento retroceder() {
        if (snapshots.size() < PASOS_POR_RETROCESO) return null;

        TableroMemento objetivo = null;
        for (int i = 0; i < PASOS_POR_RETROCESO; i++) {
            objetivo = snapshots.pop();
        }
        return objetivo;
    }

    /** Cantidad de snapshots disponibles (retrocesos posibles aún no consumidos). */
    public int getsize() {
        return snapshots.size();
    }
}
