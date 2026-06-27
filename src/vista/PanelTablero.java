package vista;

import modelo.EntidadDinamica;
import modelo.Entidad;
import modelo.Portal;
import modelo.Tablero;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.function.IntConsumer;

public class PanelTablero extends JPanel {

    private static final int TILE_SIZE = 60;
    private static final String RUTA_IMAGENES = System.getProperty("user.dir") + "/public/images/";

    private final Tablero tablero;
    private final Map<String, BufferedImage> imagenes;

    public PanelTablero(Tablero tablero) {
        this.tablero = tablero;
        this.imagenes = new HashMap<>();
        cargarImagenes();

        int cols  = tablero.getColumnas();
        int filas = tablero.getFilas();
        setPreferredSize(new Dimension(cols * TILE_SIZE, filas * TILE_SIZE));
        setFocusable(true);
    }

    public void configurarControles(IntConsumer alPresionarTecla) {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                alPresionarTecla.accept(e.getKeyCode());
            }
        });
    }

    /** Click izquierdo dispara un portal; click derecho, el otro. */
    public void configurarPortales(Runnable alClickIzquierdo, Runnable alClickDerecho) {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e))       alClickIzquierdo.run();
                else if (SwingUtilities.isRightMouseButton(e)) alClickDerecho.run();
                requestFocusInWindow();   // no perder el foco del teclado
            }
        });
    }

    private void cargarImagenes() {
        cargar("suelo",            "suelo.png");
        cargar("pared",            "muro5.png");
        cargar("destino",          "destino.png");
        cargar("cajaNormal",       "cajaNormal.png");
        cargar("sueloResbaladizo", "sueloResbaladizo5.png");

        // Cintas transportadoras (clave según CintaTransportadora.getClaveImagen())
        cargar("cinta_ARRIBA",    "cintaTransportadora/arriba.png");
        cargar("cinta_ABAJO",     "cintaTransportadora/abajo.png");
        cargar("cinta_IZQUIERDA", "cintaTransportadora/izquierda.png");
        cargar("cinta_DERECHA",   "cintaTransportadora/derecha.png");

        // Caja frágil: el sprite indica la vida restante (sana → dañada → muy dañada)
        cargar("cajaFragil",           "cajaFragil.png");
        cargar("cajaFragilDaniada",    "cajaFragilDaniada.png");
        cargar("cajaFragilMuyDaniada", "cajaFragilMuyDaniada.png");

        // Sokoban: un sprite por dirección de mirada (clave según Sokoban.getClaveImagen())
        cargar("sokobanFrente",    "sokobanFrente.png");
        cargar("sokobanEspalda",   "sokobanEspalda.png");
        cargar("sokobanIzquierda", "sokobanIzquierda.png");
        cargar("sokobanDerecha",   "sokobanDerecha.png");

        // Cerrojos  (clave según CasilleroCerrojo.getClaveImagen())
        cargar("cerrojo_AZUL",    "cerrojoAzul.png");
        cargar("cerrojo_NARANJA", "cerrojoNaranja.png");
        cargar("cerrojo_VERDE",   "cerrojoVerde.png");

        // Llaves  (clave según CajaLlave.getClaveImagen())
        cargar("llave_AZUL",      "llaveAzul.png");
        cargar("llave_NARANJA",   "llaveNaranja.png");
        cargar("llave_VERDE",     "llaveVerde.png");
        cargar("llave_MULTICANAL","llaveMulticanal.png");

        // Muros cerrados  (clave según Muro.getClaveImagen())
        cargar("muroCerrado_AZUL",    "muroCerradoAzul.png");
        cargar("muroCerrado_NARANJA", "muroCerradoNaranja.png");
        cargar("muroCerrado_VERDE",   "muroCerradoVerde.png");

        // Muros abiertos
        cargar("muroAbierto_AZUL",    "muroAbiertoAzul.png");
        cargar("muroAbierto_NARANJA", "muroAbiertoNaranja.png");
        cargar("muroAbierto_VERDE",   "muroAbiertoVerde.png");

        // Portales: un sprite por arista (clave "portal<Color>_<LADO>")
        cargar("portalAzul_ARRIBA",    "portal/azulArriba.png");
        cargar("portalAzul_ABAJO",     "portal/azulAbajo.png");
        cargar("portalAzul_IZQUIERDA", "portal/azulIzquierda.png");
        cargar("portalAzul_DERECHA",   "portal/AzulDerecha.png");
        cargar("portalNaranja_ARRIBA",    "portal/naranjaArriba.png");
        cargar("portalNaranja_ABAJO",     "portal/narajaAbajo.png");
        cargar("portalNaranja_IZQUIERDA", "portal/naranjaIzquierda.png");
        cargar("portalNaranja_DERECHA",   "portal/naranjaDerecha.png");
    }

    private void cargar(String clave, String nombreArchivo) {
        try {
            BufferedImage img = ImageIO.read(new File(RUTA_IMAGENES + nombreArchivo));
            imagenes.put(clave, img);
        } catch (IOException e) {
            System.out.println("No se pudo cargar: " + nombreArchivo);
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Entidad[][] grilla = tablero.getGrilla();

        for (int fila = 0; fila < grilla.length; fila++) {
            for (int col = 0; col < grilla[fila].length; col++) {
                int x = col * TILE_SIZE;
                int y = fila * TILE_SIZE;
                dibujarEntidad(g, grilla[fila][col], x, y);
            }
        }

        // Los portales se dibujan al final, sobre la arista de su celda.
        dibujarPortal(g, tablero.getGestorDePortales().getAzul(),    "portalAzul");
        dibujarPortal(g, tablero.getGestorDePortales().getNaranja(), "portalNaranja");
    }

    private void dibujarPortal(Graphics g, Portal portal, String prefijo) {
        if (portal == null) return;
        dibujarImagen(g, prefijo + "_" + portal.lado(),
                portal.columna() * TILE_SIZE, portal.fila() * TILE_SIZE);
    }

    private void dibujarEntidad(Graphics g, Entidad entidad, int x, int y) {
        // El suelo base se dibuja siempre de fondo en cada casillero
        dibujarImagen(g, "suelo", x, y);

        if (entidad == null) {
            return;
        }

        // 1. Polimorfismo puro: pintamos la entidad que esté en la grilla
        //    getClaveImagen() garantiza coherencia con el mapa de sprites cargado.
        dibujarImagen(g, entidad.getClaveImagen(), x, y);

        // 2. Si la celda aloja un objeto distinto de sí misma (suelo especial o
        //    muro con algo encima), lo pintamos arriba. getOcupante() devuelve la
        //    propia entidad si ya es dinámica (no hace falta repintarla).
        EntidadDinamica ocupante = entidad.getOcupante();
        if (ocupante != null && ocupante != entidad) {
            dibujarImagen(g, ocupante.getClaveImagen(), x, y);
        }
    }

    private void dibujarImagen(Graphics g, String clave, int x, int y) {
        BufferedImage img = imagenes.get(clave);
        if (img != null) {
            g.drawImage(img, x, y, TILE_SIZE, TILE_SIZE, null);
        } else {
            // Fallback visual: rectángulo magenta para detectar assets faltantes
            g.setColor(Color.MAGENTA);
            g.fillRect(x, y, TILE_SIZE, TILE_SIZE);
        }
    }
}
