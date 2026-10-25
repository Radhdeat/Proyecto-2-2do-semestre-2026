package Frontend.Menu;

// Se importa el paquete de Swing
import javax.swing.*;

// Indicamos que la clase principal es un JFrame
public class VentanaPrincipal extends JFrame {

    // Indicamos la cuenta que estara activa
    private String cuentaActual;

    // Clase para la parte visual del menu
    public VentanaPrincipal() {
        // Se le asigna un titulo a la pestaña de java
        setTitle("Zombies vs Plantas");
        // se le asigna un tamaño
        setSize(1000, 600);
        // sirve para cuando presionemos x se cierre el juego
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // es para poder centrar la pantalla
        setLocationRelativeTo(null);
        // Para que el usuario no pueda agrandar la pantalla
        setResizable(false);

        // Se llama al menu
        MenuInicio panelMenu = new MenuInicio(this);
        cambiarPantalla(panelMenu);
    }

    // Método para reemplazar el panel visible en la ventana
    public void cambiarPantalla(JPanel nuevoPanel) {
        // Indica cual sera el panel activo
        setContentPane(nuevoPanel);
        // Indica que tiene que revisar la distribucion de los elementos
        revalidate();
        // volvemos a imprimir la interfaz grafica
        repaint();
    }

    // Getters y Setters para controlar el usuario activo en toda la sesión
    public void setCuentaActual(String cuentaActual) {
        this.cuentaActual = cuentaActual;
    }

    public String getCuentaActual() {
        return cuentaActual;
    }
}