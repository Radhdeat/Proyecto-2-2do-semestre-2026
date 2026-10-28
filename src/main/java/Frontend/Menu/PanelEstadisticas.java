package Frontend.Menu;

import Backend.Estadisticas.Estadisticas;
import Backend.Estadisticas.ManejadorEstadisticas;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class PanelEstadisticas extends JPanel {
    private final VentanaPrincipal ventana;
    private final ManejadorEstadisticas manejador = new ManejadorEstadisticas();
    private final Image imagenFondo;
    private JLabel lblPartidasJugadas, lblPartidasGanadas, lblPartidasPerdidas, lblPlantasDestruidas;
    private JLabel lblZombisInvocados, lblCerebrosGastados, lblMejorTiempo, lblPodadoras;
    private final JLabel[] lblTopRapidos = new JLabel[5];
    private final JLabel[] lblTopDestructores = new JLabel[5];
    private final JLabel[] lblTopExitosos = new JLabel[5];

    public PanelEstadisticas(VentanaPrincipal ventana) {
        this.ventana = ventana;
        setLayout(null); setSize(1000, 600);
        imagenFondo = new ImageIcon("src/main/resources/Fondos/estadisticas.png").getImage();
        construirInterfaz();
        cargarDatosReales();
    }

    private void construirInterfaz() {
        lblPartidasJugadas = crearLabelDato(340,150,175,28);
        lblPartidasGanadas = crearLabelDato(340,185,175,28);
        lblPartidasPerdidas = crearLabelDato(340,220,175,28);
        lblPlantasDestruidas = crearLabelDato(340,255,175,28);
        lblZombisInvocados = crearLabelDato(640,150,175,28);
        lblCerebrosGastados = crearLabelDato(640,185,175,28);
        lblMejorTiempo = crearLabelDato(640,220,175,28);
        lblPodadoras = crearLabelDato(40,255,175,28);
        for (int i=0;i<5;i++) {
            lblTopRapidos[i] = crearLabelRanking(220,375+i*20,160,22);
            lblTopDestructores[i] = crearLabelRanking(435,375+i*20,160,22);
            lblTopExitosos[i] = crearLabelRanking(675,375+i*20,160,22);
        }
        JButton btnRegresar = new JButton(" ");
        btnRegresar.setBounds(375,490,250,45);
        btnRegresar.setOpaque(false); btnRegresar.setContentAreaFilled(false);
        btnRegresar.setBorderPainted(false); btnRegresar.setFocusPainted(false);
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.addActionListener(e -> ventana.cambiarPantalla(new MenuInicio(ventana)));
        add(btnRegresar);
    }

    private JLabel crearLabelDato(int x,int y,int ancho,int alto) {
        JLabel lbl = new JLabel("",SwingConstants.CENTER);
        lbl.setBounds(x,y,ancho,alto); lbl.setFont(new Font("Segoe UI",Font.BOLD,13));
        lbl.setForeground(new Color(40,40,40)); add(lbl); return lbl;
    }

    private JLabel crearLabelRanking(int x,int y,int ancho,int alto) {
        JLabel lbl = new JLabel("",SwingConstants.LEFT);
        lbl.setBounds(x,y,ancho,alto); lbl.setFont(new Font("Segoe UI",Font.BOLD,11));
        lbl.setForeground(new Color(40,40,40)); add(lbl); return lbl;
    }

    private void cargarDatosReales() {
        String cuenta = ventana.getCuentaActual();
        if (cuenta == null || cuenta.trim().isEmpty()) {
            mostrarSinCuenta();
        } else {
            try {
                Estadisticas e = manejador.cargar(cuenta);
                lblPartidasJugadas.setText(String.valueOf(e.getPartidasJugadas()));
                lblPartidasGanadas.setText(String.valueOf(e.getPartidasGanadas()));
                lblPartidasPerdidas.setText(String.valueOf(e.getPartidasPerdidas()));
                lblPlantasDestruidas.setText(String.valueOf(e.getPlantasDestruidas()));
                lblZombisInvocados.setText(String.valueOf(e.getZombisInvocados()));
                lblCerebrosGastados.setText(String.valueOf(e.getCerebrosGastados()));
                lblMejorTiempo.setText(formatearTiempo(e.getMejorTiempoSegundos()));
                lblPodadoras.setText(String.valueOf(e.getPodadorasUsadas()));
            } catch (IOException ex) {
                mostrarSinCuenta();
                JOptionPane.showMessageDialog(this, "No se pudieron cargar estadísticas: " + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        presentarTop(lblTopRapidos,manejador.topCinco(0),0);
        presentarTop(lblTopDestructores,manejador.topCinco(1),1);
        presentarTop(lblTopExitosos,manejador.topCinco(2),2);
    }

    private void mostrarSinCuenta() {
        lblPartidasJugadas.setText("-"); lblPartidasGanadas.setText("-");
        lblPartidasPerdidas.setText("-"); lblPlantasDestruidas.setText("-");
        lblZombisInvocados.setText("-"); lblCerebrosGastados.setText("-");
        lblMejorTiempo.setText("-"); lblPodadoras.setText("-");
    }

    private void presentarTop(JLabel[] etiquetas, Estadisticas[] top, int tipo) {
        for (int i=0;i<5;i++) {
            Estadisticas e = top[i];
            if (e == null) { etiquetas[i].setText((i+1)+". ---"); continue; }
            String valor = tipo == 0 ? formatearTiempo(e.getMejorTiempoSegundos())
                    : String.valueOf(tipo == 1 ? e.getPlantasDestruidas() : e.getPartidasGanadas());
            etiquetas[i].setText((i+1)+". "+e.getJugador()+" ("+valor+")");
            etiquetas[i].setToolTipText(e.getJugador()+" — "+valor);
        }
    }

    private String formatearTiempo(int segundos) {
        if (segundos < 0) return "--:--";
        return String.format("%02d:%02d",segundos/60,segundos%60);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (imagenFondo != null && imagenFondo.getWidth(this)>0)
            g.drawImage(imagenFondo,0,0,getWidth(),getHeight(),this);
        else { g.setColor(new Color(45,52,54)); g.fillRect(0,0,getWidth(),getHeight()); }
    }
}
