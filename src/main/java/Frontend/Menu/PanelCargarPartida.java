package Frontend.Menu;

import Backend.Cuentas.LectorNivelJson;
import Backend.Cuentas.ManejadorCuentas;
import Frontend.Jardin.PanelPartida;

import javax.swing.*;
import java.awt.*;

public class PanelCargarPartida extends JPanel {

    private VentanaPrincipal ventana;
    private ManejadorCuentas manejador;
    private JComboBox<String> comboPartidas;
    private Image fondo;

    public PanelCargarPartida(VentanaPrincipal ventana) {
        this.ventana = ventana;
        this.manejador = new ManejadorCuentas();

        setLayout(null);

        // Asegúrate de cambiar la ruta por el nombre exacto de tu nueva imagen de fondo
        fondo = new ImageIcon(
                "src/main/resources/Fondos/cargar.png"
        ).getImage();

        construirInterfaz();
        actualizarPartidas();
    }

    private void construirInterfaz() {
        // Nota: Los JLabel de título y etiqueta se eliminaron porque
        // el texto ya forma parte de la nueva imagen de fondo.

        // Configuración del JComboBox (barra de partidas)
        comboPartidas = new JComboBox<>();
        comboPartidas.setBounds(250, 265, 530, 52);
        comboPartidas.setOpaque(false);
        comboPartidas.setForeground(Color.DARK_GRAY);
        comboPartidas.setFont(new Font("Segoe UI", Font.BOLD, 14));

// Personalizamos el menú desplegable para cuando despliegues las opciones
        comboPartidas.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                // Cambiamos Component por JComponent y hacemos el cast (JComponent)
                JComponent c = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setOpaque(true);
                if (isSelected) {
                    c.setBackground(new Color(241, 196, 15)); // Color amarillo al seleccionar una opción
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(45, 45, 45)); // Fondo oscuro para la lista desplegable
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
        add(comboPartidas);

        // Botón Cargar partida (Invisible pero interactivo)
        JButton btnCargar = new JButton(" ");
        btnCargar.setBounds(250, 360, 240, 45);
        btnCargar.setOpaque(false);
        btnCargar.setContentAreaFilled(false);
        btnCargar.setBorderPainted(false);
        btnCargar.setFocusPainted(false);
        btnCargar.addActionListener(e -> cargarSeleccionada());
        add(btnCargar);

        // Botón Regresar (Invisible pero interactivo)
        JButton btnRegresar = new JButton(" ");
        btnRegresar.setBounds(525, 360, 240, 45);
        btnRegresar.setOpaque(false);
        btnRegresar.setContentAreaFilled(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFocusPainted(false);
        btnRegresar.addActionListener(e ->
                ventana.cambiarPantalla(new MenuInicio(ventana))
        );
        add(btnRegresar);
    }

    private void actualizarPartidas() {
        comboPartidas.removeAllItems();

        String[] partidas = manejador.obtenerTodasLasPartidas();

        if (partidas == null || partidas.length == 0) {
            comboPartidas.addItem("No hay partidas guardadas en las carpetas");
            comboPartidas.setEnabled(false);
            return;
        }

        comboPartidas.setEnabled(true);
        for (int i = 0; i < partidas.length; i++) {
            if (partidas[i] != null) {
                comboPartidas.addItem(partidas[i]);
            }
        }
    }

    private void cargarSeleccionada() {
        Object seleccion = comboPartidas.getSelectedItem();

        if (seleccion == null
                || seleccion.toString().contains("No hay partidas")) {
            JOptionPane.showMessageDialog(
                    this,
                    "No hay ninguna partida válida para cargar."
            );
            return;
        }

        String seleccionStr = seleccion.toString();
        String[] partes = seleccionStr.split("/");

        if (partes.length < 2) {
            JOptionPane.showMessageDialog(
                    this,
                    "Formato de ruta de partida incorrecto."
            );
            return;
        }

        String cuenta = partes[0].trim();
        String archivo = partes[1].trim();

        ventana.setCuentaActual(cuenta);

        String contenido = manejador.cargarPartida(cuenta, archivo);

        if (contenido == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo leer el archivo de la partida guardada."
            );
            return;
        }

        String rutaNivel = "src/main/resources/nivel1.json";
        String dificultadPartida = "Medio";

        String[] lineas = contenido.split("\n");
        for (int i = 0; i < lineas.length; i++) {
            String linea = lineas[i];
            if (linea.startsWith("nivelPath=")) {
                rutaNivel = linea.replace("nivelPath=", "").trim();
            }
            if (linea.startsWith("dificultad=")) {
                dificultadPartida = linea.replace("dificultad=", "").trim();
            }
        }

        LectorNivelJson lector = new LectorNivelJson();

        if (lector.cargarConfiguracion(rutaNivel)) {
            String[][] jardinDatos = lector.getJardin();
            int duracion = lector.getDuracionSegundos();
            int cerebros = lector.getCerebrosIniciales();

            PanelPartida partidaTablero = new PanelPartida(ventana, jardinDatos, duracion, cerebros, dificultadPartida);
            ventana.cambiarPantalla(partidaTablero);

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo cargar el archivo JSON del nivel asociado.",
                    "Error de Carga",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (fondo != null) {
            g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
        }
    }
}