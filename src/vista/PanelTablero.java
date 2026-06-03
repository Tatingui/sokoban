package vista;

import modelo.*;
import javax.swing.JPanel;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PanelTablero extends JPanel {

    private static final int TILE_SIZE = 60; 
    private static final String RUTA_IMAGENES = System.getProperty("user.dir") + "/sokoban/public/images/";

    private final Tablero tablero;
    private final Map<String, BufferedImage> imagenes;

    public PanelTablero(Tablero tablero) {
        this.tablero = tablero;
        this.imagenes = new HashMap<>();
        cargarImagenes();

        int cols = tablero.getGrilla()[0].length;
        int filas = tablero.getGrilla().length;
        setPreferredSize(new Dimension(cols * TILE_SIZE, filas * TILE_SIZE));
    }

    private void cargarImagenes() {
        cargar("suelo",           "suelo.png");
        cargar("pared",           "muro5.png");
        cargar("destino",         "destino.png");
        cargar("cajaNormal",      "cajaNormal.png");
        cargar("cajaFragil",      "cajaFragil.png");
        cargar("sueloResbaladizo","sueloResbaladizo5.png");
        cargar("sokoban",         "sokobanFrente.png");

        // Cerrojos
        cargar("cerrojo_AZUL",    "cerrojoAzul.png");
        cargar("cerrojo_NARANJA", "cerrojoNaranja.png");
        cargar("cerrojo_VERDE",   "cerrojoVerde.png");

        // Llaves
        cargar("llave_AZUL",      "llaveAzul.png");
        cargar("llave_NARANJA",   "llaveNaranja.png");
        cargar("llave_VERDE",     "llaveVerde.png");
        cargar("llave_MULTICANAL","llaveMulticanal.png");

        // Muros cerrados
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
                Entidad entidad = grilla[fila][col];

                dibujarEntidad(g, entidad, x, y);
            }
        }
    }

    private void dibujarEntidad(Graphics g, Entidad entidad, int x, int y) {
        // Primero dibujamos el suelo base siempre
        dibujarImagen(g, "suelo", x, y);

        if (entidad instanceof Pared) {
            dibujarImagen(g, "pared", x, y);

        } else if (entidad instanceof SueloResbaladizo) {
            dibujarImagen(g, "sueloResbaladizo", x, y);
            SueloResbaladizo sr = (SueloResbaladizo) entidad;
            if (sr.estaOcupado()) dibujarEntidadDinamica(g, sr.getObjetoEncima(), x, y);

        } else if (entidad instanceof Destino) {
            dibujarImagen(g, "destino", x, y);
            Destino d = (Destino) entidad;
            if (d.estaOcupado()) dibujarEntidadDinamica(g, d.getObjetoEncima(), x, y);

        } else if (entidad instanceof CasilleroCerrojo) {
            CasilleroCerrojo cc = (CasilleroCerrojo) entidad;
            dibujarImagen(g, "cerrojo_" + cc.getIdCanal(), x, y);
            if (cc.estaOcupado()) dibujarEntidadDinamica(g, cc.getObjetoEncima(), x, y);

        } else if (entidad instanceof Muro) {
            Muro muro = (Muro) entidad;
            String prefijo = muro.estaAbierto() ? "muroAbierto_" : "muroCerrado_";
            dibujarImagen(g, prefijo + muro.getIdCanal(), x, y);

        } else if (entidad instanceof Sokoban) {
            dibujarImagen(g, "sokoban", x, y);

        } else if (entidad instanceof CajaNormal) {
            dibujarImagen(g, "cajaNormal", x, y);

        } else if (entidad instanceof CajaFragil) {
            dibujarImagen(g, "cajaFragil", x, y);

        } else if (entidad instanceof CajaLlave) {
            dibujarCajaLlave(g, (CajaLlave) entidad, x, y);
        }
    }

    private void dibujarEntidadDinamica(Graphics g, EntidadDinamica entidad, int x, int y) {
        if (entidad instanceof Sokoban)    dibujarImagen(g, "sokoban", x, y);
        if (entidad instanceof CajaNormal) dibujarImagen(g, "cajaNormal", x, y);
        if (entidad instanceof CajaFragil) dibujarImagen(g, "cajaFragil", x, y);
        if (entidad instanceof CajaLlave)  dibujarCajaLlave(g, (CajaLlave) entidad, x, y);
    }

    private void dibujarCajaLlave(Graphics g, CajaLlave caja, int x, int y) {
        // Si tiene un solo canal usa el asset específico, si tiene varios usa multicanal
        if (caja.getCanales().size() == 1) {
            dibujarImagen(g, "llave_" + caja.getCanales().get(0), x, y);
        } else {
            dibujarImagen(g, "llave_MULTICANAL", x, y);
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