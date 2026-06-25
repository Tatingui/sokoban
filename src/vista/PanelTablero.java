package vista;

import modelo.EntidadDinamica;
import modelo.Entidad;
import modelo.EntidadEstaticaConOcupante;
import modelo.Tablero;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
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

    private void cargarImagenes() {
        cargar("suelo",            "suelo.png");
        cargar("pared",            "muro5.png");
        cargar("destino",          "destino.png");
        cargar("cajaNormal",       "cajaNormal.png");
        cargar("sueloResbaladizo", "sueloResbaladizo5.png");

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

        // 2. Si es un contenedor (suelo especial o muro abierto), pintamos también
        //    el objeto que tenga encima (si existe).
        if (entidad instanceof EntidadEstaticaConOcupante contenedor && contenedor.estaOcupado()) {
            EntidadDinamica objeto = contenedor.getObjetoEncima();
            dibujarImagen(g, objeto.getClaveImagen(), x, y);
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
