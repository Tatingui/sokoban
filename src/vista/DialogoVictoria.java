package vista;

import controlador.EstadisticasNivel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class DialogoVictoria extends JDialog {

    public DialogoVictoria(JFrame parent, String tituloNivel, EstadisticasNivel stats, 
                           boolean tieneSiguiente, Runnable accionSiguiente, Runnable accionReiniciar) {
        super(parent, "Nivel Completado", true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setResizable(false);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 40));

        // --- HEADER ---
        JLabel lblTitulo = new JLabel(tituloNivel, SwingConstants.CENTER);
        lblTitulo.setFont(new Font("SansSerif", Font.BOLD, 32));
        lblTitulo.setForeground(new Color(255, 215, 0)); // Dorado
        lblTitulo.setBorder(new EmptyBorder(25, 30, 10, 30));
        add(lblTitulo, BorderLayout.NORTH);

        // --- STATS ---
        JPanel pnlStats = new JPanel(new GridLayout(4, 1, 0, 10));
        pnlStats.setOpaque(false);
        pnlStats.setBorder(new EmptyBorder(10, 50, 20, 50));

        pnlStats.add(crearLabelStat("Movimientos:", String.valueOf(stats.movimientos())));
        pnlStats.add(crearLabelStat("Empujes:", String.valueOf(stats.empujes())));
        pnlStats.add(crearLabelStat("Usos de Deshacer:", String.valueOf(stats.deshacerUsados())));
        
        JLabel lblPuntaje = new JLabel("PUNTAJE: " + stats.puntaje(), SwingConstants.CENTER);
        lblPuntaje.setFont(new Font("SansSerif", Font.BOLD, 24));
        lblPuntaje.setForeground(new Color(100, 255, 100));
        lblPuntaje.setBorder(new EmptyBorder(15, 0, 0, 0));
        pnlStats.add(lblPuntaje);

        add(pnlStats, BorderLayout.CENTER);

        // --- BOTONES ---
        JPanel pnlBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        pnlBotones.setOpaque(false);

        JButton btnReiniciar = crearBoton("Reiniciar Nivel", new Color(200, 60, 60));
        btnReiniciar.addActionListener(e -> {
            dispose();
            if (accionReiniciar != null) accionReiniciar.run();
        });
        pnlBotones.add(btnReiniciar);

        if (tieneSiguiente) {
            JButton btnSiguiente = crearBoton("Siguiente Nivel", new Color(60, 180, 75));
            btnSiguiente.addActionListener(e -> {
                dispose();
                if (accionSiguiente != null) accionSiguiente.run();
            });
            pnlBotones.add(btnSiguiente);
        } else {
            JButton btnSalir = crearBoton("Salir", new Color(100, 100, 150));
            btnSalir.addActionListener(e -> {
                dispose();
                System.exit(0);
            });
            pnlBotones.add(btnSalir);
        }

        add(pnlBotones, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(parent);
    }

    private JPanel crearLabelStat(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout(30, 0));
        panel.setOpaque(false);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("SansSerif", Font.PLAIN, 18));
        lblTitulo.setForeground(new Color(200, 200, 220));

        JLabel lblValor = new JLabel(valor);
        lblValor.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblValor.setForeground(Color.WHITE);

        panel.add(lblTitulo, BorderLayout.WEST);
        panel.add(lblValor, BorderLayout.EAST);
        return panel;
    }

    private JButton crearBoton(String texto, Color bg) {
        JButton btn = new JButton(texto);
        btn.setFont(new Font("SansSerif", Font.BOLD, 16));
        btn.setBackground(bg);
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setPreferredSize(new Dimension(170, 40));
        return btn;
    }
}
