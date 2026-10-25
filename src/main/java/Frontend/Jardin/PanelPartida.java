package Frontend.Jardin;

import Frontend.Menu.MenuInicio;
import Frontend.Menu.VentanaPrincipal;

import javax.swing.*;
import java.awt.*;

public class PanelPartida extends JPanel {

    private VentanaPrincipal ventana;
    private PanelJardinTablero panelJardin;
    private Image imagenFondoTablero;

    // Atributos privados estrictos (Cumpliendo reglas)
    private JLabel lblCerebros;
    private JLabel lblTiempo;
    private JLabel lblDificultad;
    private Thread hiloCronometro;
    private boolean juegoActivo;
    private int segundosRestantes;
    private int cerebrosActuales;
    private String zombiSeleccionado;
    private String dificultadJuego;

    public PanelPartida(VentanaPrincipal ventana, String[][] jardinDatos, int duracionSegundos, int cerebrosIniciales, String dificultad) {
        this.ventana = ventana;
        this.segundosRestantes = duracionSegundos;
        this.cerebrosActuales = cerebrosIniciales;
        this.dificultadJuego = (dificultad != null && !dificultad.isEmpty()) ? dificultad : "Medio";
        this.juegoActivo = true;
        this.zombiSeleccionado = null;

        setSize(1000, 600);
        setLayout(null);

        imagenFondoTablero = new ImageIcon("src/main/resources/Tablero.png").getImage();

        construirHUD();
        construirTableroJardin(jardinDatos);
        iniciarCronometroNativo();
    }

    private void construirHUD() {
        lblCerebros = new JLabel(String.valueOf(cerebrosActuales), SwingConstants.CENTER);
        lblCerebros.setFont(new Font("Impact", Font.BOLD, 18));
        lblCerebros.setForeground(Color.BLACK);
        lblCerebros.setBounds(80, 24, 45, 25);
        add(lblCerebros);

        // --- 1. BOTONES INVISIBLES DE ZOMBIS (6 zombis) ---
        String[] tiposZombis = {
                "ZombiNormal", "ZombiCono", "ZombiObstaculo",
                "ZombiDeportista", "ZombiBailarin", "ZombiGargantuar"
        };

        int xInicio = 145;
        int anchoBtn = 50;
        int altoBtn = 55;
        int separacion = 60;

        for (int i = 0; i < tiposZombis.length; i++) {
            final String tipo = tiposZombis[i];
            JButton btnZombi = new JButton();
            btnZombi.setBounds(xInicio + (i * separacion), 10, anchoBtn, altoBtn);

            // --- PROPIEDADES DE INVISIBILIDAD ---
            btnZombi.setContentAreaFilled(false);
            btnZombi.setBorderPainted(false);
            btnZombi.setFocusPainted(false);
            btnZombi.setOpaque(false);

            btnZombi.addActionListener(e -> {
                zombiSeleccionado = tipo;
            });
            add(btnZombi);
        }

        // --- 2. BOTÓN DE PAUSA INVISIBLE (Ubicado en el 7mo espacio) ---
        JButton btnPausa = new JButton();
        btnPausa.setBounds(xInicio + (6 * separacion), 10, anchoBtn, altoBtn);

        // --- PROPIEDADES DE INVISIBILIDAD ---
        btnPausa.setContentAreaFilled(false);
        btnPausa.setBorderPainted(false);
        btnPausa.setFocusPainted(false);
        btnPausa.setOpaque(false);

        btnPausa.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Juego en pausa (Funcionalidad pendiente)");
        });
        add(btnPausa);

        lblTiempo = new JLabel(formatearTiempo(segundosRestantes), SwingConstants.CENTER);
        lblTiempo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTiempo.setBounds(650, 22, 55, 25);
        add(lblTiempo);

        // --- INDICADOR DE DIFICULTAD EN EL HUD ---
        lblDificultad = new JLabel(dificultadJuego, SwingConstants.CENTER);
        lblDificultad.setFont(new Font("Impact", Font.BOLD, 18));
        lblDificultad.setForeground(Color.BLACK);
        lblDificultad.setBounds(755, 33, 80, 20);
        add(lblDificultad);

        // --- BOTÓN SALIR INVISIBLE ---
        JButton btnSalir = new JButton();
        btnSalir.setBounds(865, 15, 110, 42);

        btnSalir.setContentAreaFilled(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setOpaque(false);

        btnSalir.addActionListener(e -> {
            juegoActivo = false;
            ventana.cambiarPantalla(new MenuInicio(ventana));
        });
        add(btnSalir);
    }

    private void construirTableroJardin(String[][] jardinDatos) {
        panelJardin = new PanelJardinTablero(jardinDatos);

        // Coordenadas exactas solicitadas: X=94, Y=102, Ancho=768 (862-94), Alto=477 (579-102)
        panelJardin.setBounds(94, 95, 755, 449);

        add(panelJardin);
    }

    private void iniciarCronometroNativo() {
        hiloCronometro = new Thread(() -> {
            while (juegoActivo && segundosRestantes > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }

                if (!juegoActivo) break;

                segundosRestantes--;

                SwingUtilities.invokeLater(() -> {
                    lblTiempo.setText(formatearTiempo(segundosRestantes));
                    if (segundosRestantes <= 0 && juegoActivo) {
                        juegoActivo = false;
                        JOptionPane.showMessageDialog(PanelPartida.this, "¡Se ha agotado el tiempo del nivel!");
                    }
                });
            }
        });
        hiloCronometro.start();
    }

    private String formatearTiempo(int segundosTotales) {
        int minutos = segundosTotales / 60;
        int segundos = segundosTotales % 60;
        return String.format("%02d:%02d", minutos, segundos);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondoTablero != null) {
            g.drawImage(imagenFondoTablero, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(50, 120, 50));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}