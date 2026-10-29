
package Frontend.Menu;

import Frontend.Jardin.PanelPartida;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class PanelCargarPartida extends JPanel {

    private final VentanaPrincipal ventana;
    private JComboBox<String> comboPartidas;
    private Image fondo;

    private File[] archivosEncontrados = new File[0];
    private String[] cuentasEncontradas = new String[0];

    public PanelCargarPartida(VentanaPrincipal ventana) {
        this.ventana = ventana;

        setLayout(null);
        setSize(1000, 600);

        fondo = new ImageIcon(
                getClass().getResource("/Fondos/cargar.png")
        ).getImage();

        construirInterfaz();
        actualizarPartidas();
    }

    private void construirInterfaz() {

        comboPartidas = new JComboBox<>();
        comboPartidas.setBounds(250, 265, 530, 52);
        comboPartidas.setOpaque(false);
        comboPartidas.setForeground(Color.DARK_GRAY);
        comboPartidas.setFont(
                new Font("Segoe UI", Font.BOLD, 14)
        );

        comboPartidas.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(
                    JList<?> list,
                    Object value,
                    int index,
                    boolean isSelected,
                    boolean cellHasFocus) {

                JComponent c = (JComponent)
                        super.getListCellRendererComponent(
                                list, value, index,
                                isSelected, cellHasFocus
                        );

                c.setOpaque(true);

                if (isSelected) {
                    c.setBackground(new Color(241, 196, 15));
                    c.setForeground(Color.BLACK);
                } else {
                    c.setBackground(new Color(45, 45, 45));
                    c.setForeground(Color.WHITE);
                }

                return c;
            }
        });

        add(comboPartidas);

        // Botón Cargar partida
        JButton btnCargar = new JButton(" ");
        btnCargar.setBounds(250, 360, 240, 45);
        btnCargar.setOpaque(false);
        btnCargar.setContentAreaFilled(false);
        btnCargar.setBorderPainted(false);
        btnCargar.setFocusPainted(false);
        btnCargar.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        btnCargar.addActionListener(
                e -> cargarSeleccionada()
        );

        add(btnCargar);

        // Botón Regresar
        JButton btnRegresar = new JButton(" ");
        btnRegresar.setBounds(525, 360, 240, 45);
        btnRegresar.setOpaque(false);
        btnRegresar.setContentAreaFilled(false);
        btnRegresar.setBorderPainted(false);
        btnRegresar.setFocusPainted(false);
        btnRegresar.setCursor(
                new Cursor(Cursor.HAND_CURSOR)
        );

        btnRegresar.addActionListener(e ->
                ventana.cambiarPantalla(
                        new MenuInicio(ventana)
                )
        );

        add(btnRegresar);
    }

    // Busca las partidas de todas las cuentas.
    private void actualizarPartidas() {

        comboPartidas.removeAllItems();

        archivosEncontrados = new File[0];
        cuentasEncontradas = new String[0];

        File raiz = new File("cuentas");

        if (!raiz.exists() || !raiz.isDirectory()) {
            mostrarSinPartidas();
            return;
        }

        // Busca en todas las cuentas aunque no exista
        // una sesión activa al abrir el programa.
        File[] carpetas = raiz.listFiles(
                File::isDirectory
        );

        if (carpetas == null) {
            mostrarSinPartidas();
            return;
        }

        for (int i = 0; i < carpetas.length; i++) {

            File carpetaPartidas = new File(
                    carpetas[i], "partidas"
            );

            File[] archivos = carpetaPartidas.listFiles(
                    archivo -> archivo.isFile()
                            && archivo.getName()
                            .toLowerCase()
                            .endsWith(".dat")
            );

            if (archivos == null) {
                continue;
            }

            for (int j = 0; j < archivos.length; j++) {
                agregarArchivo(
                        archivos[j],
                        carpetas[i].getName()
                );
            }
        }

        if (archivosEncontrados.length == 0) {
            mostrarSinPartidas();
            return;
        }

        ordenarPartidas();

        // Mostrar solamente el nombre del archivo.
        // La cuenta sigue guardada internamente.
        for (int i = 0; i < archivosEncontrados.length; i++) {

            String nombre = archivosEncontrados[i].getName();

            comboPartidas.addItem(nombre);
        }

        comboPartidas.setEnabled(true);
        comboPartidas.setSelectedIndex(0);
    }

    private void mostrarSinPartidas() {

        comboPartidas.removeAllItems();
        comboPartidas.addItem("No hay partidas guardadas");
        comboPartidas.setEnabled(false);
    }

    // Agrega un archivo conservando su propietario.
    private void agregarArchivo(File archivo, String cuenta) {

        int cantidad = archivosEncontrados.length;

        File[] nuevosArchivos = new File[cantidad + 1];
        String[] nuevasCuentas = new String[cantidad + 1];

        for (int i = 0; i < cantidad; i++) {

            nuevosArchivos[i] = archivosEncontrados[i];
            nuevasCuentas[i] = cuentasEncontradas[i];
        }

        nuevosArchivos[cantidad] = archivo;
        nuevasCuentas[cantidad] = cuenta;

        archivosEncontrados = nuevosArchivos;
        cuentasEncontradas = nuevasCuentas;
    }

    // Ordena los guardados del más reciente al más antiguo.
    private void ordenarPartidas() {

        for (int i = 0; i < archivosEncontrados.length - 1; i++) {

            for (int j = i + 1;
                 j < archivosEncontrados.length; j++) {

                if (archivosEncontrados[j].lastModified()
                        > archivosEncontrados[i].lastModified()) {

                    File archivoTemporal = archivosEncontrados[i];

                    archivosEncontrados[i] = archivosEncontrados[j];
                    archivosEncontrados[j] = archivoTemporal;

                    String cuentaTemporal = cuentasEncontradas[i];

                    cuentasEncontradas[i] = cuentasEncontradas[j];
                    cuentasEncontradas[j] = cuentaTemporal;
                }
            }
        }
    }

    private void cargarSeleccionada() {

        int indice = comboPartidas.getSelectedIndex();

        if (!comboPartidas.isEnabled()
                || indice < 0
                || indice >= archivosEncontrados.length) {

            JOptionPane.showMessageDialog(
                    this,
                    "No hay ninguna partida válida para cargar."
            );
            return;
        }

        File archivo = archivosEncontrados[indice];
        String cuenta = cuentasEncontradas[indice];

        if (!archivo.exists() || !archivo.isFile()) {

            JOptionPane.showMessageDialog(
                    this,
                    "El archivo seleccionado ya no existe."
            );

            actualizarPartidas();
            return;
        }

        if (cuenta == null || cuenta.trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró la cuenta de esta partida."
            );
            return;
        }

        // Utilizamos la ruta real, no el texto mostrado
        // en el JComboBox.

        // Este método vincula la cuenta y reconstruye
        // la partida binaria guardada.

        PanelPartida.cargarArchivoDesdeMenu(
                ventana,
                cuenta,
                archivo
        );
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (fondo != null && fondo.getWidth(this) > 0) {

            g.drawImage(
                    fondo,
                    0, 0,
                    getWidth(),
                    getHeight(),
                    this
            );

        } else {

            g.setColor(new Color(45, 52, 54));
            g.fillRect(
                    0, 0,
                    getWidth(),
                    getHeight()
            );
        }
    }
}
