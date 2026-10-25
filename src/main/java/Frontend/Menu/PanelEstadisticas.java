package Frontend.Menu;

import javax.swing.*;
import java.awt.*;

public class PanelEstadisticas extends JPanel {

    private VentanaPrincipal ventana;
    private Image imagenFondo;

    // Etiquetas para las Estadísticas de Cuenta (8 métricas)
    private JLabel lblPartidasJugadas, lblPartidasGanadas, lblPartidasPerdidas, lblPlantasDestruidas;
    private JLabel lblZombisInvocados, lblCerebrosGastados, lblMejorTiempo, lblPodadoras;

    // Arreglos para los Rankings Globales (Top 5 en cada categoría)
    private JLabel[] lblTopRapidos = new JLabel[5];
    private JLabel[] lblTopDestructores = new JLabel[5];
    private JLabel[] lblTopExitosos = new JLabel[5];

    public PanelEstadisticas(VentanaPrincipal ventana) {
        this.ventana = ventana;

        setLayout(null);
        setSize(1000, 600);

        // Carga de la nueva imagen de fondo de estadísticas
        imagenFondo = new ImageIcon(
                "src/main/resources/estadisticas.png"
        ).getImage();

        construirInterfaz();
        cargarDatosEjemplo(); // Puedes reemplazar esto con los datos reales de tu backend/manejador
    }

    private void construirInterfaz() {
        // --- ESTADÍSTICAS DE CUENTA (Columna Izquierda) ---
        lblPartidasJugadas = crearLabelDato(340, 150, 175, 28);
        lblPartidasGanadas = crearLabelDato(340, 185, 175, 28);
        lblPartidasPerdidas = crearLabelDato(340, 220, 175, 28);
        lblPlantasDestruidas = crearLabelDato(340, 255, 175, 28);

        // --- ESTADÍSTICAS DE CUENTA (Columna Derecha) ---
        lblZombisInvocados = crearLabelDato(640, 150, 175, 28);
        lblCerebrosGastados = crearLabelDato(640, 185, 175, 28);
        lblMejorTiempo = crearLabelDato(640, 220, 175, 28);
        lblPodadoras = crearLabelDato(40, 255, 175, 28);

        // --- RANKING GLOBAL (Top 5 más rápidos - Columna 1) ---
        for (int i = 0; i < 5; i++) {
            lblTopRapidos[i] = crearLabelRanking(220, 375 + (i * 20), 160, 22);
        }

        // --- RANKING GLOBAL (Top 5 mayores destructores - Columna 2) ---
        for (int i = 0; i < 5; i++) {
            lblTopDestructores[i] = crearLabelRanking(435, 375 + (i * 20), 160, 22);
        }

        // --- RANKING GLOBAL (Top 5 jugadores más exitosos - Columna 3) ---
        for (int i = 0; i < 5; i++) {
            lblTopExitosos[i] = crearLabelRanking(675, 375 + (i * 20), 160, 22);
        }

        // Botón Regresar (Invisible pero interactivo sobre el botón rojo inferior)
        JButton btnRegresar = new JButton(" ");
        btnRegresar.setBounds(375, 490, 250, 45);
        btnRegresar.setOpaque(false);
        btnRegresar.setContentAreaFilled(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.addActionListener(e ->
                ventana.cambiarPantalla(new MenuInicio(ventana))
        );
        add(btnRegresar);
    }

    // Método auxiliar para crear las etiquetas de datos de cuenta centrados
    private JLabel crearLabelDato(int x, int y, int ancho, int alto) {
        JLabel lbl = new JLabel("", SwingConstants.CENTER);
        lbl.setBounds(x, y, ancho, alto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(new Color(40, 40, 40));
        add(lbl);
        return lbl;
    }

    // Método auxiliar para crear las etiquetas del ranking (alineadas a la izquierda)
    private JLabel crearLabelRanking(int x, int y, int ancho, int alto) {
        JLabel lbl = new JLabel("", SwingConstants.LEFT);
        lbl.setBounds(x, y, ancho, alto);
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lbl.setForeground(new Color(40, 40, 40));
        add(lbl);
        return lbl;
    }

    // Método de ejemplo para simular la asignación de estadísticas (conéctalo aquí a tu clase manejadora)
    private void cargarDatosEjemplo() {
        // Estadísticas de cuenta generales
        lblPartidasJugadas.setText("15");
        lblPartidasGanadas.setText("10");
        lblPartidasPerdidas.setText("5");
        lblPlantasDestruidas.setText("42");

        lblZombisInvocados.setText("180");
        lblCerebrosGastados.setText("650");
        lblMejorTiempo.setText("02:15 min");
        lblPodadoras.setText("8");

        // Rankings globales (Ejemplo con los 5 puestos)
        for (int i = 0; i < 5; i++) {
            lblTopRapidos[i].setText((i + 1) + ". Jugador " + (i + 1) + " (0" + (i + 1) + ":30)");
            lblTopDestructores[i].setText((i + 1) + ". MasterZombi (" + (50 - i * 5) + ")");
            lblTopExitosos[i].setText((i + 1) + ". ProPlayer (" + (100 - i * 10) + " pts)");
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imagenFondo != null) {
            g.drawImage(imagenFondo, 0, 0, getWidth(), getHeight(), this);
        } else {
            g.setColor(new Color(45, 52, 54));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}