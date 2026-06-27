package modelo;

import java.util.List;
import java.util.Map;

/**
 * GoF Memento — almacena una instantánea completa del estado mutable del juego.
 *
 * Encapsulación:
 *  - El constructor y todos los campos son package-private: solo {@link Tablero}
 *    (Originator, mismo paquete) puede crear instancias y leer su contenido.
 *  - {@link controlador.ControladorJuego} (Caretaker) mantiene referencias opacas;
 *    el único accessor público es {@link #getContadorMovimientos()}, que el HUD
 *    necesita para actualizar la UI al deshacer.
 *
 * El estado interno de cada entidad (resistencia, activación, apertura) se guarda
 * como un {@code Object} opaco que la propia entidad produce y consume
 * ({@link Entidad#capturarEstadoMemento()} / {@link Entidad#aplicarEstadoMemento(Object)}),
 * de modo que el Memento no conoce los tipos concretos.
 *
 * Inmutabilidad: las colecciones se copian defensivamente con List.copyOf / Map.copyOf.
 */
public final class TableroMemento {

    // ── Campos package-private: solo Tablero puede leerlos ───────────────────
    final int                    jugadorFila;
    final int                    jugadorColumna;
    final Direccion              miradaJugador;
    final List<SnapshotDinamico> dinamicos;
    final Map<String, Object>    estadosEstaticos;  // clave "fila,columna" → estado de la celda estática
    final Map<String, Boolean>   canales;           // clave idCanal → abierto

    private final int contadorMovimientos;

    // ── Constructor package-private ───────────────────────────────────────────
    TableroMemento(int jugadorFila,
                   int jugadorColumna,
                   Direccion miradaJugador,
                   int contadorMovimientos,
                   List<SnapshotDinamico> dinamicos,
                   Map<String, Object>    estadosEstaticos,
                   Map<String, Boolean>   canales) {
        this.jugadorFila         = jugadorFila;
        this.jugadorColumna      = jugadorColumna;
        this.miradaJugador       = miradaJugador;
        this.contadorMovimientos = contadorMovimientos;
        this.dinamicos           = List.copyOf(dinamicos);
        this.estadosEstaticos    = Map.copyOf(estadosEstaticos);
        this.canales             = Map.copyOf(canales);
    }

    /**
     * Único accessor público: el Caretaker lo usa para sincronizar el HUD
     * al deshacer sin acceder al resto del estado interno.
     */
    public int getContadorMovimientos() {
        return contadorMovimientos;
    }

    // ── Value-object de snapshot (package-private) ───────────────────────────

    /**
     * Captura dónde estaba una EntidadDinamica y su estado interno opaco.
     * El campo {@code sobreContenedor} distingue los dos modelos de celda:
     *  false → la entidad ocupaba grilla[f][c] directamente.
     *  true  → la entidad era el ocupante de un contenedor (suelo especial o
     *          muro) en grilla[f][c].
     */
    static final class SnapshotDinamico {
        final int             fila;
        final int             columna;
        final boolean         sobreContenedor;
        final EntidadDinamica entidad;
        final Object          estado;   // estado interno capturado por la entidad (o null)

        SnapshotDinamico(int fila, int columna, boolean sobreContenedor,
                         EntidadDinamica entidad, Object estado) {
            this.fila            = fila;
            this.columna         = columna;
            this.sobreContenedor = sobreContenedor;
            this.entidad         = entidad;
            this.estado          = estado;
        }
    }
}
