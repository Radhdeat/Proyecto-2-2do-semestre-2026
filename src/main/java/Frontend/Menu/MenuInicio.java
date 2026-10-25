package Frontend.Menu;

// importamos la extension de Swing
import javax.swing.*;
// importamos cosas como el cursor o imagnes
import java.awt.*;
// importamos la calse que detecta acciones como un click
import java.awt.event.ActionEvent;
// importamos la clase que se encarga de ejecutar la accion de un botton
import java.awt.event.ActionListener;

// e un Jpanel ya que con esto podemos agregar imagenes, botones, etc...
public class MenuInicio extends JPanel {

    // Declaramos una referencia para que sepa cual jpanel es
    private VentanaPrincipal inicio;

    // constructor donde instanciamos los atriburos
    public MenuInicio(VentanaPrincipal ventana) {

        this.inicio = ventana;

        // tamaño del panel
        setSize(1000, 600);
        // Layout nulo para poder poner las posas donde yo quiera
        setLayout(null);

        // llamamos a nuetros botones
        cargarFondoYBotones();
    }

    // metodo enfocado en poner botnes y fondo
    private void cargarFondoYBotones() {

        // la diagonal en el nombre indica que hara la busqueda desde la raiz de recursos
        java.net.URL ruta = getClass().getResource("/MENU.jpg");

        // si la ruta si existe se genera la imagen
        if (ruta != null) {

            // Trabajamos la imagenes como icocnos y esta busca la imagen desde la ruta
            ImageIcon iconoOriginal = new ImageIcon(ruta);
            /*
            / recibe la imagne y le asigna un tamaño
            / Image.SCALE_SMOOTH solicita un escalado suave para intentar conservar una apariencia visual de mejor calidad.
            */

            Image imgEscalada = iconoOriginal.getImage().getScaledInstance(1000, 600, Image.SCALE_SMOOTH);
            // usamos un jlabel para poder mostrar la imagen
            JLabel fondo = new JLabel(new ImageIcon(imgEscalada));
            // se le asigna una posicion
            fondo.setBounds(0, 0, 1000, 600);

            // tamaño y coordenadas de los botones
            int ancho = 339, alto = 77, xCentro = 330;

            // declaramos las posiciones, tamaños y atributos de los botones, pero ojo el centro ya se definio
            JButton btnNuevaPartida = crearBotonTransparente(xCentro, 151, ancho, alto);
            JButton btnCargarPartida = crearBotonTransparente(xCentro, 245, ancho, alto);
            JButton btnEstadisticas = crearBotonTransparente(xCentro, 335, ancho, alto);
            JButton btnSalir = crearBotonTransparente(xCentro, 420, ancho, alto);

            // Se llama a las acciones de los botones
            btnNuevaPartida.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent eventos) {

                    // Instanciamos la nueva pantalla y le decimos a la ventana que la muestre
                    PanelNuevaPartida panelCuentas = new PanelNuevaPartida(inicio);
                    inicio.cambiarPantalla(panelCuentas);
                }
            });

            // Botón Cargar partida
            btnCargarPartida.addActionListener(eventos -> {
                // lama al jpanel para cargar una partida
                PanelCargarPartida panelCargar = new PanelCargarPartida(inicio);
                inicio.cambiarPantalla(panelCargar);
            });

            // Botón Estadísticas
            btnEstadisticas.addActionListener(eventos -> {
                // llamamos al panel para las estadisticas
                PanelEstadisticas panelEstadisticas = new PanelEstadisticas(inicio);
                inicio.cambiarPantalla(panelEstadisticas);
            });

            // Acción del botón salir
            btnSalir.addActionListener(eventos -> System.exit(0));

           // Agregamos todo al JPanel
            add(btnNuevaPartida);
            add(btnCargarPartida);
            add(btnEstadisticas);
            add(btnSalir);
            add(fondo);
        }
    }


    private JButton crearBotonTransparente(int x, int y, int ancho, int alto) {
        // Ponesmos los atributos de cada botton
        // creamos un boton sin texto
        JButton btn = new JButton();
        // dejamos las cordenadas como parametros
        btn.setBounds(x, y, ancho, alto);
        // se imprime sin un fondo
        btn.setOpaque(false);
        // se evita que cree un area así no lo pone con un color celeste
        btn.setContentAreaFilled(false);
        // quitamos el borde
        btn.setBorderPainted(false);
        // evita que se imprima el indicador
        btn.setFocusPainted(false);
        // Cambiamos el cursor
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}