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
 * Inmutabilidad:
 *  - Las colecciones se copian defensivamente en el constructor con List.copyOf /
 *    Map.copyOf, de modo que ninguna modificación posterior al tablero altera el
 *    snapshot ya guardado.
 */
public final class TableroMemento {

    // ── Campos package-private: solo Tablero puede leerlos ───────────────────
    final int                            jugadorFila;
    final int                            jugadorColumna;
    final Direccion                      miradaJugador;
    final List<SnapshotDinamico>         dinamicos;
    final Map<String, SnapshotCerrojo>   cerrojos;   // clave: "fila,columna"
    final Map<String, SnapshotCanal>     canales;    // clave: idCanal
    final Map<String, Boolean>           muros;      // clave: "fila,columna" → abierto

    private final int contadorMovimientos;

    // ── Constructor package-private ───────────────────────────────────────────
    TableroMemento(int jugadorFila,
                   int jugadorColumna,
                   Direccion miradaJugador,
                   int contadorMovimientos,
                   List<SnapshotDinamico>       dinamicos,
                   Map<String, SnapshotCerrojo> cerrojos,
                   Map<String, SnapshotCanal>   canales,
                   Map<String, Boolean>         muros) {
        this.jugadorFila          = jugadorFila;
        this.jugadorColumna       = jugadorColumna;
        this.miradaJugador        = miradaJugador;
        this.contadorMovimientos  = contadorMovimientos;
        this.dinamicos            = List.copyOf(dinamicos);
        this.cerrojos             = Map.copyOf(cerrojos);
        this.canales              = Map.copyOf(canales);
        this.muros                = Map.copyOf(muros);
    }

    /**
     * Único accessor público: el Caretaker lo usa para sincronizar el HUD
     * al deshacer sin acceder al resto del estado interno.
     */
    public int getContadorMovimientos() {
        return contadorMovimientos;
    }

    // ── Value-objects de snapshot (package-private) ──────────────────────────

    /**
     * Captura dónde estaba una EntidadDinamica y, si es CajaFragil, cuál era
     * su resistencia en ese instante.
     * El campo {@code sobreContenedor} distingue los dos modelos de celda:
     *  false → la entidad ocupaba grilla[f][c] directamente.
     *  true  → la entidad era el objetoEncima de un contenedor (suelo especial o
     *          muro abierto) en grilla[f][c].
     */
    static final class SnapshotDinamico {
        final int              fila;
        final int              columna;
        final boolean          sobreContenedor;
        final EntidadDinamica  entidad;
        /** Resistencia de CajaFragil en el momento del snapshot; -1 para otros tipos. */
        final int              resistencia;

        SnapshotDinamico(int fila, int columna, boolean sobreContenedor,
                         EntidadDinamica entidad, int resistencia) {
            this.fila            = fila;
            this.columna         = columna;
            this.sobreContenedor = sobreContenedor;
            this.entidad         = entidad;
            this.resistencia     = resistencia;
        }
    }

    /** Estado interno del CasilleroCerrojo (activo/inactivo) en el momento del snapshot. */
    static final class SnapshotCerrojo {
        final boolean activo;

        SnapshotCerrojo(boolean activo) {
            this.activo = activo;
        }
    }

    /** Estado interno del Canal (abierto/cerrado) en el momento del snapshot. */
    static final class SnapshotCanal {
        final boolean abierto;

        SnapshotCanal(boolean abierto) {
            this.abierto = abierto;
        }
    }
}
