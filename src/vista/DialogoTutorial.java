package vista;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class DialogoTutorial extends JDialog {

    private static final String RUTA_IMAGENES = System.getProperty("user.dir") + "/public/images/";

    public DialogoTutorial(JFrame parent) {
        super(parent, "Tutorial - Sokoban", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 40));

        // --- HEADER ---
        JLabel lblTitulo = new JLabel("SOKOBAN", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 48));
        lblTitulo.setForeground(new Color(255, 215, 0)); // Dorado
        lblTitulo.setBorder(new EmptyBorder(20, 0, 20, 0));
        add(lblTitulo, BorderLayout.NORTH);

        // --- MAIN CONTENT ---
        JPanel pnlCentro = new JPanel(new GridLayout(1, 2, 20, 0));
        pnlCentro.setOpaque(false);
        pnlCentro.setBorder(new EmptyBorder(10, 20, 20, 20));

        // LEFT: Controles
        JPanel pnlControles = new JPanel();
        pnlControles.setLayout(new BoxLayout(pnlControles, BoxLayout.Y_AXIS));
        pnlControles.setOpaque(false);
        
        JLabel lblControlesTitulo = crearLabel("Controles Principales", true);
        pnlControles.add(lblControlesTitulo);
        pnlControles.add(Box.createRigidArea(new Dimension(0, 15)));

        pnlControles.add(crearFilaControl("Flechas / WASD", "Movimiento de Sokoban"));
        pnlControles.add(crearFilaControl("Z", "Deshacer último movimiento"));
        pnlControles.add(crearFilaControl("R", "Reiniciar nivel completo"));
        pnlControles.add(crearFilaControl("Click Izquierdo", "Disparar portal AZUL"));
        pnlControles.add(crearFilaControl("Click Derecho", "Disparar portal NARANJA"));

        // RIGHT: Entidades
        JPanel pnlEntidades = new JPanel();
        pnlEntidades.setLayout(new BoxLayout(pnlEntidades, BoxLayout.Y_AXIS));
        pnlEntidades.setOpaque(false);

        JLabel lblEntidadesTitulo = crearLabel("Entidades Especiales", true);
        pnlEntidades.add(lblEntidadesTitulo);
        pnlEntidades.add(Box.createRigidArea(new Dimension(0, 15)));

        pnlEntidades.add(crearFilaEntidad("muroCerradoAzul.png", "Muro especial. Los portales pueden traspasarlo."));
        pnlEntidades.add(crearFilaEntidad("cajaFragil.png", "Caja Corazón. ¡Se rompe tras 3 empujes!"));
        
        // Para la llave y el cerrojo usamos dos iconos juntos
        JPanel pnlCerrojo = crearFilaDobleEntidad("llaveAzul.png", "cerrojoAzul.png", "Llevá las bolas a los cerrojos para abrir puertas.");
        pnlEntidades.add(pnlCerrojo);

        pnlCentro.add(pnlControles);
        pnlCentro.add(pnlEntidades);
        add(pnlCentro, BorderLayout.CENTER);

        // --- FOOTER ---
        JPanel pnlInferior = new JPanel(new BorderLayout());
        pnlInferior.setOpaque(false);
        pnlInferior.setBorder(new EmptyBorder(0, 20, 20, 20));

        JLabel lblSecreto = new JLabel("Profe Pepe, lo retamos a pasar el tercer nivel. Recuerde el limite de empujes de la caja Corazon.");
        lblSecreto.setFont(new Font("SansSerif", Font.ITALIC, 12));
        lblSecreto.setForeground(new Color(150, 150, 170));
        lblSecreto.setHorizontalAlignment(SwingConstants.CENTER);
        lblSecreto.setBorder(new EmptyBorder(0, 0, 15, 0));

        JButton btnEntendido = new JButton("Entendido");
        btnEntendido.setFont(new Font("SansSerif", Font.BOLD, 18));
        btnEntendido.setBackground(new Color(60, 180, 75));
        btnEntendido.setForeground(Color.WHITE);
        btnEntendido.setFocusPainted(false);
        btnEntendido.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnEntendido.setPreferredSize(new Dimension(200, 40));
        btnEntendido.addActionListener(e -> dispose());

        JPanel pnlBoton = new JPanel(new FlowLayout(FlowLayout.CENTER));
        pnlBoton.setOpaque(false);
        pnlBoton.add(btnEntendido);

        pnlInferior.add(lblSecreto, BorderLayout.NORTH);
        pnlInferior.add(pnlBoton, BorderLayout.CENTER);

        add(pnlInferior, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private JLabel crearLabel(String texto, boolean titulo) {
        JLabel lbl = new JLabel(texto);
        lbl.setForeground(Color.WHITE);
        if (titulo) {
            lbl.setFont(new Font("SansSerif", Font.BOLD, 22));
            lbl.setForeground(new Color(100, 200, 255));
        } else {
            lbl.setFont(new Font("SansSerif", Font.PLAIN, 14));
        }
        return lbl;
    }

    private JPanel crearFilaControl(String tecla, String accion) {
        JPanel panel = new JPanel(new BorderLayout(10, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));

        JLabel lblTecla = new JLabel("[" + tecla + "]");
        lblTecla.setFont(new Font("Monospaced", Font.BOLD, 14));
        lblTecla.setForeground(new Color(255, 200, 100));
        lblTecla.setPreferredSize(new Dimension(130, 25));

        JLabel lblAccion = crearLabel(accion, false);

        panel.add(lblTecla, BorderLayout.WEST);
        panel.add(lblAccion, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearFilaEntidad(String archivo, String descripcion) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));

        JLabel lblIcono = new JLabel();
        try {
            BufferedImage img = ImageIO.read(new File(RUTA_IMAGENES + archivo));
            Image scaled = img.getScaledInstance(48, 48, Image.SCALE_SMOOTH);
            lblIcono.setIcon(new ImageIcon(scaled));
        } catch (IOException e) {
            lblIcono.setText("[IMG]");
            lblIcono.setForeground(Color.WHITE);
        }
        lblIcono.setPreferredSize(new Dimension(48, 48));

        JLabel lblDesc = crearLabel("<html><div style='width: 200px;'>" + descripcion + "</div></html>", false);

        panel.add(lblIcono, BorderLayout.WEST);
        panel.add(lblDesc, BorderLayout.CENTER);
        return panel;
    }

    private JPanel crearFilaDobleEntidad(String archivo1, String archivo2, String descripcion) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(8, 0, 8, 0));

        JPanel pnlIconos = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlIconos.setOpaque(false);

        for (String archivo : new String[]{archivo1, archivo2}) {
            JLabel lblIcono = new JLabel();
            try {
                BufferedImage img = ImageIO.read(new File(RUTA_IMAGENES + archivo));
                Image scaled = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                lblIcono.setIcon(new ImageIcon(scaled));
            } catch (IOException e) {}
            pnlIconos.add(lblIcono);
        }

        JLabel lblDesc = crearLabel("<html><div style='width: 150px;'>" + descripcion + "</div></html>", false);

        panel.add(pnlIconos, BorderLayout.WEST);
        panel.add(lblDesc, BorderLayout.CENTER);
        return panel;
    }
}
