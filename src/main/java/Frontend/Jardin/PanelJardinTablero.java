package Frontend.Jardin;

import Backend.Modelo.Entidades.Plantas.*;
import javax.swing.*;
import java.awt.*;

public class PanelJardinTablero extends JPanel {
    private int filas;
    private int columnas;
    private JButton[][] matrizBotones;
    private Planta[][] matrizPlantas;
    private Image imagenPastoOriginal;

    public PanelJardinTablero(String[][] jsonJardin) {
        this.filas = jsonJardin.length;
        this.columnas = jsonJardin[0].length;

        // GridLayout sin espacios entre celdas
        this.setLayout(new GridLayout(filas, columnas, 0, 0));
        this.setOpaque(false);

        this.matrizBotones = new JButton[filas][columnas];
        this.matrizPlantas = new Planta[filas][columnas];

        cargarImagenPasto();
        inicializarComponentes(jsonJardin);
    }

    private void cargarImagenPasto() {
        ImageIcon iconoOriginal = new ImageIcon("src/main/resources/pasto.png");
        if (iconoOriginal.getIconWidth() > 0) {
            this.imagenPastoOriginal = iconoOriginal.getImage();
        } else {
            this.imagenPastoOriginal = null;
        }
    }

    private void inicializarComponentes(String[][] jsonJardin) {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {

                JButton botonCasilla = new JButton() {
                    @Override
                    protected void paintComponent(Graphics g) {
                        if (imagenPastoOriginal != null) {
                            g.drawImage(imagenPastoOriginal, 0, 0, getWidth(), getHeight(), this);
                        } else {
                            g.setColor(new Color(110, 180, 60));
                            g.fillRect(0, 0, getWidth(), getHeight());
                        }
                        super.paintComponent(g);
                    }
                };

                botonCasilla.setFocusPainted(false);
                // --- QUITAMOS EL BORDE PARA QUE NO SE VEAN LÍNEAS ---
                botonCasilla.setBorder(null);
                botonCasilla.setMargin(new Insets(0, 0, 0, 0));
                botonCasilla.setContentAreaFilled(false);

                String tipoPlanta = jsonJardin[i][j];

                if (!tipoPlanta.equals("VACIO")) {
                    matrizPlantas[i][j] = crearInstanciaPlanta(tipoPlanta, i, j);
                    botonCasilla.setText(obtenerSiglasPlanta(tipoPlanta));
                    botonCasilla.setHorizontalTextPosition(SwingConstants.CENTER);
                    botonCasilla.setVerticalTextPosition(SwingConstants.CENTER);
                    botonCasilla.setForeground(Color.WHITE);
                    botonCasilla.setFont(new Font("Arial", Font.BOLD, 12));
                } else {
                    matrizPlantas[i][j] = null;
                    botonCasilla.setText("");
                }

                matrizBotones[i][j] = botonCasilla;
                this.add(botonCasilla);
            }
        }
    }

    private Planta crearInstanciaPlanta(String tipo, int fila, int columna) {
        switch (tipo) {
            case "LANZAGUISANTES":
                return new Lanzaguisantes(fila, columna);
            case "PATATAPUM":
                return new Patatapum(fila, columna);
            case "HIELAGUISANTES":
                return new Hielaguisantes(fila, columna);
            case "NUEZ":
                return new Nuez(fila, columna);
            case "CARRONIVORA":
                return new PlantaCarronivora(fila, columna);
            case "PINCHOHIERBA":
                return new PinchoHierba(fila, columna);
            case "BIPETIDORA":
                return new Bipetidora(fila, columna);
            default:
                return null;
        }
    }

    private String obtenerSiglasPlanta(String tipo) {
        switch (tipo) {
            case "LANZAGUISANTES": return "LNZ";
            case "PATATAPUM": return "PTA";
            case "HIELAGUISANTES": return "HIE";
            case "NUEZ": return "NUZ";
            case "CARRONIVORA": return "CAR";
            case "PINCHOHIERBA": return "PNC";
            case "BIPETIDORA": return "BIP";
            default: return "";
        }
    }

    public JButton[][] getMatrizBotones() {
        return matrizBotones;
    }

    public Planta[][] getMatrizPlantas() {
        return matrizPlantas;
    }
}