package Frontend.Menu;

import Backend.Cuentas.LectorNivelJson;
import Backend.Cuentas.ManejadorCuentas;
import Frontend.Jardin.PanelPartida;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;

public class PanelNuevaPartida extends JPanel {

    private VentanaPrincipal ventanaActiva;
    private ManejadorCuentas manejador;
    private JTextField txtNombrePartida;
    private JComboBox<String> cmbDificultad;
    private JTextField txtRutaJson;
    private String rutaArchivoSeleccionado = "";
    private Image imagenFondo;

    public PanelNuevaPartida(VentanaPrincipal ventana) {
        this.ventanaActiva = ventana;
        this.manejador = new ManejadorCuentas();

        setSize(1000, 600);
        setLayout(null);

        // Actualizado al nombre de tu nueva imagen de fondo
        imagenFondo = new ImageIcon(
                "src/main/resources/Fondos/nueva.png"
        ).getImage();

        construirInterfaz();
    }

    private void construirInterfaz() {
        // NOTA: Se eliminaron el panel tarjeta y todos los JLabel de texto
        // porque la nueva imagen ya incluye los títulos y etiquetas impresos.

        // Campo de texto: Nombre de la partida (Transparente sobre la ranura beige)
        txtNombrePartida = new JTextField();
        txtNombrePartida.setBounds(260, 180, 480, 32);
        txtNombrePartida.setFont(new Font("Segoe UI", Font.BOLD, 14));
        txtNombrePartida.setForeground(Color.BLACK);
        txtNombrePartida.setOpaque(false);
        txtNombrePartida.setBackground(new Color(0, 0, 0, 0));
        txtNombrePartida.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(txtNombrePartida);

        // Selector de Dificultad (JComboBox transparente e integrado)
        String[] dificultades = {"Fácil", "Medio", "Difícil"};
        cmbDificultad = new JComboBox<>(dificultades);
        cmbDificultad.setBounds(250, 250, 500, 35);
        cmbDificultad.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cmbDificultad.setForeground(Color.BLACK);
        cmbDificultad.setOpaque(false);
        cmbDificultad.setBackground(new Color(0, 0, 0, 0));
        cmbDificultad.setBorder(BorderFactory.createEmptyBorder());

        // Renderizador para evitar colores extraños y combinar con el diseño
        cmbDificultad.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JComponent c = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                c.setOpaque(true);
                if (index == -1) {
                    c.setBackground(new Color(236, 240, 241)); // Fondo claro para la caja principal
                    c.setForeground(Color.BLACK);
                } else if (isSelected) {
                    c.setBackground(new Color(241, 196, 15)); // Amarillo al seleccionar
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(45, 45, 45));   // Fondo oscuro para la lista desplegable
                    c.setForeground(Color.WHITE);
                }
                return c;
            }
        });
        add(cmbDificultad);

        // Campo de texto: Ruta del archivo JSON
        txtRutaJson = new JTextField();
        txtRutaJson.setBounds(260, 325, 360, 32);
        txtRutaJson.setEditable(false);
        txtRutaJson.setFont(new Font("Segoe UI", Font.BOLD, 13));
        txtRutaJson.setForeground(Color.BLACK);
        txtRutaJson.setOpaque(false);
        txtRutaJson.setBackground(new Color(0, 0, 0, 0));
        txtRutaJson.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        add(txtRutaJson);

        // Botón Examinar (Invisible pero interactivo sobre el botón azul de la imagen)
        JButton btnExaminar = new JButton(" ");
        btnExaminar.setBounds(640, 325, 110, 32);
        btnExaminar.setOpaque(false);
        btnExaminar.setContentAreaFilled(false);
        btnExaminar.setBorderPainted(false);
        btnExaminar.setFocusPainted(false);
        btnExaminar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnExaminar.addActionListener(e -> seleccionarArchivoJson());
        add(btnExaminar);

        // Botón Crear partida (Invisible sobre el botón verde de la imagen)
        JButton btnCrear = new JButton(" ");
        btnCrear.setBounds(355, 385, 300, 45);
        btnCrear.setOpaque(false);
        btnCrear.setContentAreaFilled(false);
        btnCrear.setBorderPainted(false);
        btnCrear.setFocusPainted(false);
        btnCrear.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCrear.addActionListener(e -> crearPartida());
        add(btnCrear);

        // Botón Regresar al menú (Invisible sobre el botón rojo de la imagen)
        JButton btnRegresar = new JButton(" ");
        btnRegresar.setBounds(355, 445, 300, 38);
        btnRegresar.setOpaque(false);
        btnRegresar.setContentAreaFilled(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegresar.addActionListener(e ->
                ventanaActiva.cambiarPantalla(
                        new MenuInicio(ventanaActiva)
                )
        );
        add(btnRegresar);
    }

    private void seleccionarArchivoJson() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setCurrentDirectory(new File("src/main/resources"));

        int resultado = fileChooser.showOpenDialog(this);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File archivoSeleccionado = fileChooser.getSelectedFile();
            rutaArchivoSeleccionado = archivoSeleccionado.getAbsolutePath();
            txtRutaJson.setText(archivoSeleccionado.getName());
        }
    }

    private void crearPartida() {
        String nombre = txtNombrePartida.getText().trim();

        if (nombre.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes ingresar un nombre para la partida.",
                    "Nombre requerido",
                    JOptionPane.WARNING_MESSAGE
            );
            txtNombrePartida.requestFocus();
            return;
        }

        if (rutaArchivoSeleccionado.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Debes seleccionar un archivo JSON para el nivel.",
                    "Nivel requerido",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String dificultadSeleccionada = (String) cmbDificultad.getSelectedItem();
        String cuentaActiva = ventanaActiva.getCuentaActual();

        if (cuentaActiva == null || cuentaActiva.isEmpty()) {
            cuentaActiva = "JugadorPrueba";
            ventanaActiva.setCuentaActual(cuentaActiva);
        }

        String estadoInicial = "dificultad=" + dificultadSeleccionada +
                "\nnivelPath=" + rutaArchivoSeleccionado +
                "\nzombisInvocados=0\ncerebrosGastados=0\nplantasDestruidas=0\n";

        boolean guardadoExitoso = manejador.guardarPartida(cuentaActiva, nombre, estadoInicial);

        if (!guardadoExitoso) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo crear el archivo de la partida en disco.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            return;
        }

        LectorNivelJson lector = new LectorNivelJson();

        if (lector.cargarConfiguracion(rutaArchivoSeleccionado)) {
            String[][] jardinDatos = lector.getJardin();
            int duracion = lector.getDuracionSegundos();
            int cerebros = lector.getCerebrosIniciales();

            PanelPartida partidaTablero = new PanelPartida(ventanaActiva, jardinDatos, duracion, cerebros, dificultadSeleccionada);
            ventanaActiva.cambiarPantalla(partidaTablero);

        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "No se pudo cargar el archivo de configuración del nivel.",
                    "Error de Carga",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (imagenFondo != null) {
            g.drawImage(
                    imagenFondo,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this
            );
        } else {
            g.setColor(new Color(45, 52, 54));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
}