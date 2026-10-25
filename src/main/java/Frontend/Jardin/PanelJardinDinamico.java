package Frontend.Jardin;

import javax.swing.*;
import java.awt.*;

public class PanelJardinDinamico extends JPanel {
    private int filas;
    private int columnas;
    private JButton[][] matrizBotones;

    public PanelJardinDinamico(int filas, int columnas, String[][] datosIniciales) {
        this.filas = filas;
        this.columnas = columnas;

        // Configuramos un GridLayout dinámico basado en el JSON
        this.setLayout(new GridLayout(filas, columnas, 2, 2));
        this.matrizBotones = new JButton[filas][columnas];

        inicializarCuadricula(datosIniciales);
    }

    private void inicializarCuadricula(String[][] datosIniciales) {
        for (int i = 0; i < filas; i++) {
            for (int j = 0; j < columnas; j++) {
                JButton casilla = new JButton();
                casilla.setFocusPainted(false);

                // Verificamos qué planta viene en la configuración inicial del JSON
                if (datosIniciales != null && i < datosIniciales.length && j < datosIniciales[i].length) {
                    String planta = datosIniciales[i][j];
                    if (!planta.equals("VACIO")) {
                        casilla.setText(planta); // Aquí puedes cambiar por iconos gráficos más adelante
                    } else {
                        casilla.setText("");
                    }
                }

                matrizBotones[i][j] = casilla;
                this.add(casilla); // Añadimos el botón al panel de Swing
            }
        }
    }

    // Getters explícitos (sin Lombok)
    public JButton[][] getMatrizBotones() {
        return matrizBotones;
    }
}