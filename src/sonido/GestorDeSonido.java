package sonido;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

/**
 * Gestor de sonido (Singleton). Carga una vez los clips del juego y los
 * reproduce ante los eventos: música de fondo en bucle, efectos de movimiento,
 * portales, colocación correcta y victoria.
 *
 * Si un archivo falta o tiene un formato no soportado (Java estándar no reproduce
 * MP3), ese sonido queda nulo y su reproducción es un no-op: el juego sigue
 * andando sin audio para ese evento.
 */
public class GestorDeSonido {

    private static final GestorDeSonido INSTANCIA = new GestorDeSonido();

    public static GestorDeSonido getInstancia() { return INSTANCIA; }

    // Campo de INSTANCIA (no estático): se inicializa antes del cuerpo del
    // constructor, evitando el problema de orden de inicialización estática con
    // el singleton eager (si fuera static, correría DESPUÉS del constructor → null).
    private final String ruta =
            System.getProperty("user.dir") + "/public/Sound-library/SoundFX/";

    private final Clip musica;
    private final Clip abrirPortal;
    private final Clip cajaEnDestino;
    private final Clip moverCaja;
    private final Clip caminar;
    private final Clip victoria;

    private GestorDeSonido() {
        System.out.println("[sonido] Cargando clips desde: " + ruta);
        musica        = cargar("Sokoban Online OST - Blissful.wav");
        abrirPortal   = cargar("Abrir Portal.wav");
        cajaEnDestino = cargar("Caja en destino.wav");
        moverCaja     = cargar("Mover Caja.wav");
        caminar       = cargar("Sokoban Caminando.wav");
        victoria      = cargar("Sonido de Victoria.wav");

        int efectos = 0;
        for (Clip c : new Clip[]{abrirPortal, cajaEnDestino, moverCaja, caminar, victoria}) {
            if (c != null) efectos++;
        }
        System.out.println("[sonido] Listo. Música: " + (musica != null ? "OK" : "FALTA")
                + " | Efectos cargados: " + efectos + "/5");
    }

    // ── API pública ──────────────────────────────────────────────────────────

    /** Arranca la música de fondo en bucle (idempotente: si ya suena, no reinicia). */
    public void reproducirMusicaFondo() {
        if (musica == null || musica.isRunning()) return;
        musica.setFramePosition(0);
        musica.loop(Clip.LOOP_CONTINUOUSLY);
    }

    public void abrirPortal()   { reproducirEfecto(abrirPortal); }
    public void cajaEnDestino() { reproducirEfecto(cajaEnDestino); }
    public void moverCaja()     { reproducirEfecto(moverCaja); }
    public void caminar()       { reproducirEfecto(caminar); }
    public void victoria()      { reproducirEfecto(victoria); }

    // ── Internos ──────────────────────────────────────────────────────────────

    private void reproducirEfecto(Clip clip) {
        if (clip == null) return;
        clip.stop();
        clip.setFramePosition(0);
        clip.start();
    }

    private Clip cargar(String archivo) {
        File f = new File(ruta + archivo);
        if (!f.exists()) {
            System.out.println("[sonido] No existe el archivo: " + f.getPath());
            return null;
        }
        try {
            AudioInputStream audio = AudioSystem.getAudioInputStream(f);
            Clip clip = AudioSystem.getClip();
            clip.open(audio);   // open() bufferea todo el stream en memoria
            return clip;
        } catch (Exception e) {
            System.out.println("[sonido] No se pudo cargar '" + archivo + "': " + e);
            return null;
        }
    }
}
