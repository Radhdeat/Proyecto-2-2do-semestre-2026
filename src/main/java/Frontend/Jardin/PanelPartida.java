package Frontend.Jardin;

import Backend.Modelo.Entidades.Zombis.*;
import Backend.Modelo.Tablero;
import Backend.Motor.MotorJuego;
import Frontend.Menu.MenuInicio;
import Frontend.Menu.VentanaPrincipal;

import Backend.Modelo.Entidades.Plantas.*;
import Backend.Modelo.Entidades.Guisantes.Guisantes;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class PanelPartida extends JPanel {
    private int zombisInvocados = 0;
    private int duracionInicial;
    private boolean resultadoMostrado = false;
    private VentanaPrincipal ventana;
    private PanelJardinTablero panelJardin;
    private Image imagenFondoTablero;
    private Image imagenCerebro;

    private JLabel lblCerebros;
    private JLabel lblTiempo;
    private JLabel lblDificultad;

    private Thread hiloCronometro;
    private Thread hiloGeneradorCerebros;

    private boolean juegoActivo;
    private boolean cerebroRecogidoEnClic;

    private int segundosRestantes;
    private int cerebrosActuales;

    private String zombiSeleccionado;
    private String dificultadJuego;

    private Cerebro[] listaCerebros;
    private final int MAX_CEREBROS = 100;

    private Tablero tablero;
    private MotorJuego motor;
    private Image imagenZombiNormal;
    private Image imagenZombiCaraCono;
    private Image imagenZombiSaltador;
    private Image imagenZombiDeportista;
    private Image imagenZombiBailon;
    private Image imagenZombistein;

    private Image imagenPodadora;

    private Image imagenGuisante;
    private Image imagenGuisanteCongelado;

    private static final int ANCHO_PODADORA = 55;
    private static final int ALTO_PODADORA = 55;

    private static final int ANCHO_GUISANTE = 25;
    private static final int ALTO_GUISANTE = 25;


    public PanelPartida(
            VentanaPrincipal ventana,
            String[][] jardinDatos,
            int duracionSegundos,
            int cerebrosIniciales,
            String dificultad) {

        this.ventana = ventana;
        this.segundosRestantes = Math.max(0, duracionSegundos);
        this.duracionInicial = this.segundosRestantes;
        this.cerebrosActuales = Math.max(0, cerebrosIniciales);

        this.dificultadJuego =
                (dificultad != null && !dificultad.isEmpty())
                        ? dificultad
                        : "Medio";

        this.juegoActivo = true;
        this.zombiSeleccionado = null;
        this.cerebroRecogidoEnClic = false;

        this.listaCerebros = new Cerebro[MAX_CEREBROS];

        setSize(1000, 600);
        setLayout(null);
        setOpaque(true);

        imagenFondoTablero = new ImageIcon("src/main/resources/Fondos/Tablero.png").getImage();

        imagenCerebro = new ImageIcon("src/main/resources/Zombis/cerebro.png").getImage();

        imagenZombiNormal = new ImageIcon("src/main/resources/Zombis/normal.png").getImage();

        imagenZombiCaraCono = new ImageIcon("src/main/resources/Zombis/cono.png").getImage();

        imagenZombiSaltador = new ImageIcon("src/main/resources/Zombis/saltador.png").getImage();

        imagenZombiDeportista = new ImageIcon("src/main/resources/Zombis/deportista.png").getImage();

        imagenZombiBailon = new ImageIcon("src/main/resources/Zombis/bailon.png").getImage();

        imagenZombistein = new ImageIcon("src/main/resources/Zombis/zombistein.png").getImage();

        imagenPodadora = new ImageIcon("src/main/resources/Recursos/Podadora.png").getImage();

        imagenGuisante = new ImageIcon("src/main/resources/Recursos/guisante.png").getImage();

        imagenGuisanteCongelado = new ImageIcon("src/main/resources/Recursos/guisanteCongelado.png").getImage();

        // Crear el modelo antes de construir el tablero visual.
        if (jardinDatos == null || jardinDatos.length == 0 || jardinDatos[0] == null || jardinDatos[0].length == 0) {
            throw new IllegalArgumentException(
                    "Los datos del jardín no pueden estar vacíos."
            );
        }

        tablero = new Tablero(jardinDatos.length, jardinDatos[0].length
        );

        construirHUD();
        construirTableroJardin(jardinDatos);

        // El motor actualizará el juego y solicitará repintar la interfaz.
        motor = new MotorJuego(tablero, () -> {

            panelJardin.actualizarPlantas(tablero.getCasillas());

            repaint();
        });

        motor.setEventoVictoria(() -> finalizarPartida(true));

        iniciarCronometroNativo();
        iniciarGeneradoresCerebros();
    }

    private void construirHUD() {

        lblCerebros = new JLabel(
                String.valueOf(cerebrosActuales),
                SwingConstants.CENTER
        );

        lblCerebros.setFont(new Font("Impact", Font.BOLD, 18));
        lblCerebros.setForeground(Color.BLACK);
        lblCerebros.setBounds(80, 24, 45, 25);

        add(lblCerebros);

        String[] tiposZombis = {
                "ZombiNormal",
                "ZombiCaraCono",
                "ZombiSaltador",
                "ZombiDeportista",
                "ZombiBailon",
                "Zombistein"
        };

        int xInicio = 145;
        int anchoBtn = 50;
        int altoBtn = 55;
        int separacion = 60;

        for (int i = 0; i < tiposZombis.length; i++) {

            final String tipo = tiposZombis[i];

            JButton btnZombi = new JButton();

            btnZombi.setBounds(
                    xInicio + (i * separacion),
                    10,
                    anchoBtn,
                    altoBtn
            );

            btnZombi.setContentAreaFilled(false);
            btnZombi.setBorderPainted(false);
            btnZombi.setFocusPainted(false);
            btnZombi.setOpaque(false);
            btnZombi.setToolTipText(tipo);

            btnZombi.addActionListener(e -> {

                if (!juegoActivo) {
                    return;
                }

                zombiSeleccionado = tipo;

                System.out.println(
                        "Zombi seleccionado: " + zombiSeleccionado
                );
            });

            add(btnZombi);
        }

        // Botón de pausa.
        JButton btnPausa = new JButton();

        btnPausa.setBounds(
                xInicio + (6 * separacion),
                10,
                anchoBtn,
                altoBtn
        );

        btnPausa.setContentAreaFilled(false);
        btnPausa.setBorderPainted(false);
        btnPausa.setFocusPainted(false);
        btnPausa.setOpaque(false);
        btnPausa.setToolTipText("Pausa");

        btnPausa.addActionListener(e -> {

            if (juegoActivo) {
                JOptionPane.showMessageDialog(
                        this,
                        "La pausa todavía no está implementada."
                );
            }
        });

        add(btnPausa);

        lblTiempo = new JLabel(
                formatearTiempo(segundosRestantes),
                SwingConstants.CENTER
        );

        lblTiempo.setFont(new Font("Segoe UI", Font.BOLD, 20));
        lblTiempo.setBounds(650, 22, 65, 25);

        add(lblTiempo);

        lblDificultad = new JLabel(
                dificultadJuego,
                SwingConstants.CENTER
        );

        lblDificultad.setFont(new Font("Impact", Font.BOLD, 18));
        lblDificultad.setForeground(Color.BLACK);
        lblDificultad.setBounds(755, 33, 80, 20);

        add(lblDificultad);

        // Botón invisible para conservar el diseño de la imagen.
        JButton btnSalir = new JButton();

        btnSalir.setBounds(865, 15, 110, 42);
        btnSalir.setContentAreaFilled(false);
        btnSalir.setBorderPainted(false);
        btnSalir.setFocusPainted(false);
        btnSalir.setOpaque(false);
        btnSalir.setToolTipText("Salir del juego");

        btnSalir.addActionListener(e -> salirDelJuego());

        add(btnSalir);
    }

    private void construirTableroJardin(String[][] jardinDatos) {

        panelJardin = new PanelJardinTablero(
                jardinDatos,
                new PanelJardinTablero.FilaClickListener() {

                    @Override
                    public void onFilaClicked(int filaSeleccionada) {

                        if (!juegoActivo) {
                            return;
                        }

                        // Si se recogió un cerebro con este clic,
                        // no se debe invocar un zombi.
                        if (cerebroRecogidoEnClic) {
                            cerebroRecogidoEnClic = false;
                            return;
                        }

                        intentarInvocarZombiEnFila(filaSeleccionada);
                    }
                });

        panelJardin.setBounds(94, 95, 755, 449);

        add(panelJardin);

        Planta[][] plantasIniciales = panelJardin.getMatrizPlantas();

        for (int fila = 0; fila < tablero.getFilas(); fila++) {

            for (int columna = 0; columna < tablero.getColumnas(); columna++) {

                Planta planta = plantasIniciales[fila][columna];

                if (planta != null) {
                    tablero.sembrarPlanta(planta);
                }
            }
        }

        JButton[][] botones = panelJardin.getMatrizBotones();


        for (int fila = 0; fila < botones.length; fila++) {

            for (int columna = 0;
                 columna < botones[fila].length;
                 columna++) {

                JButton boton = botones[fila][columna];

                boton.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mousePressed(MouseEvent e) {

                        Point punto = SwingUtilities.convertPoint(
                                boton,
                                e.getPoint(),
                                PanelPartida.this
                        );

                        cerebroRecogidoEnClic =
                                verificarClicCerebro(
                                        punto.x,
                                        punto.y
                                );
                    }
                });
            }
        }
    }

    private void dibujarPodadoras(Graphics g) {
        if (tablero == null || panelJardin == null) {
            return;
        }

        int filas = tablero.getFilas();
        if (filas <= 0) {
            return;
        }

        double altoFila = (double) panelJardin.getHeight() / filas;
        boolean[] disponibles = tablero.getPodadorasActivas();
        boolean[] moviendose = tablero.getPodadorasEnMovimiento();
        double[] posiciones = tablero.getPosicionesPodadoras();

        for (int fila = 0; fila < filas; fila++) {
            if (!disponibles[fila] && !moviendose[fila]) {
                continue;
            }

            int x = panelJardin.getX() + (int) Math.round(posiciones[fila]);
            int y = panelJardin.getY()
                    + (int) Math.round(fila * altoFila
                    + (altoFila - ALTO_PODADORA) / 2.0);

            if (imagenPodadora != null && imagenPodadora.getWidth(this) > 0) {
                g.drawImage(imagenPodadora, x, y,
                        ANCHO_PODADORA, ALTO_PODADORA, this);
            } else {
                g.setColor(Color.RED);
                g.fillRect(x, y, ANCHO_PODADORA, ALTO_PODADORA);
            }
        }
    }

    public void intentarInvocarZombiEnFila(int filaSeleccionada) {

        if (!juegoActivo) {
            return;
        }

        if (zombiSeleccionado == null
                || zombiSeleccionado.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Primero selecciona un zombi del menú superior."
            );

            return;
        }

        if (filaSeleccionada < 0
                || filaSeleccionada >= tablero.getFilas()) {

            JOptionPane.showMessageDialog(
                    this,
                    "La fila seleccionada no es válida."
            );

            return;
        }

        int costo = obtenerCostoZombi(zombiSeleccionado);

        if (cerebrosActuales < costo) {

            JOptionPane.showMessageDialog(
                    this,
                    "¡No tienes suficientes cerebros! Necesitas " + costo
            );

            return;
        }

        // La posición X se mide desde el extremo izquierdo del jardín.
        // El zombi comienza en el extremo izquierdo del jardín.
        double posicionInicial = 0;

        // Crear el tipo de zombi seleccionado.
        Zombi zombi;

        switch (zombiSeleccionado) {
            case "ZombiNormal":
                zombi = new ZombiComun(filaSeleccionada, posicionInicial);
                break;
            case "ZombiCaraCono":
                zombi = new ZombiCaraCono(filaSeleccionada, posicionInicial);
                break;
            case "ZombiSaltador":
                zombi = new ZombiSaltador(filaSeleccionada, posicionInicial);
                break;
            case "ZombiDeportista":
                zombi = new ZombiDeportista(filaSeleccionada, posicionInicial);
                break;
            case "ZombiBailon":
                zombi = new ZombiBailon(filaSeleccionada, posicionInicial);
                break;
            case "Zombistein":
                zombi = new Zombistein(filaSeleccionada, posicionInicial);
                break;
            default:
                JOptionPane.showMessageDialog(this, "Tipo de zombi desconocido.");
                return;
        }

        tablero.agregarZombi(zombi);
        zombisInvocados++;

        // Cobrar solamente cuando se haya creado y agregado.
        cerebrosActuales -= costo;
        lblCerebros.setText(String.valueOf(cerebrosActuales));

        // Iniciar el motor. Si ya está activo, no crea otro hilo.
        motor.iniciarJuego();

        zombiSeleccionado = null;

        repaint();

        System.out.println(
                "Zombi invocado en la fila "
                        + (filaSeleccionada + 1)
        );
    }

    private int obtenerCostoZombi(String tipo) {

        switch (tipo) {

            case "ZombiNormal":
                return 50;

            case "ZombiCaraCono":
                return 75;

            case "ZombiSaltador":
                return 100;

            case "ZombiDeportista":
                return 125;

            case "ZombiBailon":
                return 150;

            case "Zombistein":
                return 300;

            default:
                return 50;
        }
    }

    private void iniciarGeneradoresCerebros() {

        hiloGeneradorCerebros = new Thread(() -> {

            while (juegoActivo
                    && !Thread.currentThread().isInterrupted()) {

                try {
                    Thread.sleep(5000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                SwingUtilities.invokeLater(() -> {

                    if (juegoActivo) {

                        int posX = 94
                                + (int) (Math.random() * 715);

                        int posY = 95
                                + (int) (Math.random() * 409);

                        agregarCerebroFlotante(posX, posY);
                    }
                });
            }
        });

        hiloGeneradorCerebros.start();
    }

    private void agregarCerebroFlotante(int x, int y) {

        if (!juegoActivo) {
            return;
        }

        for (int i = 0; i < listaCerebros.length; i++) {

            if (listaCerebros[i] == null
                    || !listaCerebros[i].isActivo()
                    || listaCerebros[i].haExpirado()) {

                listaCerebros[i] = new Cerebro(x, y);

                repaint();

                return;
            }
        }
    }

    private boolean verificarClicCerebro(int px, int py) {

        if (!juegoActivo) {
            return false;
        }

        for (int i = 0; i < listaCerebros.length; i++) {

            Cerebro cerebro = listaCerebros[i];

            if (cerebro != null
                    && cerebro.isActivo()
                    && !cerebro.haExpirado()
                    && cerebro.contienePunto(px, py)) {

                cerebrosActuales += 25;

                lblCerebros.setText(
                        String.valueOf(cerebrosActuales)
                );

                cerebro.setActivo(false);
                listaCerebros[i] = null;

                repaint();

                return true;
            }
        }

        return false;
    }

    private void iniciarCronometroNativo() {

        hiloCronometro = new Thread(() -> {

            while (juegoActivo && segundosRestantes > 0) {

                try {
                    Thread.sleep(1000);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                if (!juegoActivo) {
                    break;
                }

                SwingUtilities.invokeLater(() -> {
                    if (!juegoActivo) {
                        return;
                    }

                    segundosRestantes = Math.max(0, segundosRestantes - 1);

                    for (int i = 0; i < listaCerebros.length; i++) {
                        if (listaCerebros[i] != null
                                && listaCerebros[i].haExpirado()) {
                            listaCerebros[i].setActivo(false);
                            listaCerebros[i] = null;
                        }
                    }

                    lblTiempo.setText(formatearTiempo(segundosRestantes));
                    repaint();

                    if (segundosRestantes == 0) {
                        finalizarPartida(false);
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

    private void finalizarPartida(boolean victoria) {
        if (!SwingUtilities.isEventDispatchThread()) {
            SwingUtilities.invokeLater(() -> finalizarPartida(victoria));
            return;
        }

        if (resultadoMostrado || !juegoActivo) {
            return;
        }

        resultadoMostrado = true;
        juegoActivo = false;

        detenerGeneradorCerebros();
        if (hiloCronometro != null) {
            hiloCronometro.interrupt();
        }
        if (motor != null) {
            motor.detenerJuego();
        }

        int transcurrido = duracionInicial - segundosRestantes;
        int destruidas = motor != null ? motor.getPlantasDestruidas() : 0;

        JOptionPane.showMessageDialog(
                this,
                (victoria ? "¡VICTORIA!" : "DERROTA")
                        + "\nTiempo transcurrido: " + formatearTiempo(transcurrido)
                        + "\nPlantas destruidas: " + destruidas
                        + "\nZombis invocados: " + zombisInvocados,
                "Resultado de la partida",
                JOptionPane.INFORMATION_MESSAGE
        );

        ventana.cambiarPantalla(new MenuInicio(ventana));
    }

    private void salirDelJuego() {

        if (!juegoActivo) {
            return;
        }

        juegoActivo = false;

        detenerGeneradorCerebros();

        if (hiloCronometro != null) {
            hiloCronometro.interrupt();
        }

        if (motor != null) {
            motor.detenerJuego();
        }

        ventana.cambiarPantalla(new MenuInicio(ventana));
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        if (imagenFondoTablero != null
                && imagenFondoTablero.getWidth(this) > 0) {

            g.drawImage(
                    imagenFondoTablero,
                    0,
                    0,
                    getWidth(),
                    getHeight(),
                    this
            );

        } else {

            g.setColor(new Color(50, 120, 50));
            g.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    private void dibujarGuisantes(Graphics g) {

        if (tablero == null || panelJardin == null) {
            return;
        }

        int filas = tablero.getFilas();

        if (filas <= 0) {
            return;
        }

        int altoFila = panelJardin.getHeight() / filas;

        for (int i = 0; i < tablero.getListaGuisantes().getTamaño(); i++) {

            Guisantes guisante = tablero.getListaGuisantes().obtener(i);

            if (guisante == null || !guisante.estaActivo()) {
                continue;
            }

            int x = panelJardin.getX()
                    + (int) guisante.getPosicionX();

            int y = panelJardin.getY()
                    + guisante.getFila() * altoFila
                    + (altoFila - ALTO_GUISANTE) / 2;

            Image imagen;

            if (guisante.esCongelante()) {
                imagen = imagenGuisanteCongelado;
            } else {
                imagen = imagenGuisante;
            }

            if (imagen != null && imagen.getWidth(this) > 0) {

                g.drawImage(
                        imagen,
                        x,
                        y,
                        ANCHO_GUISANTE,
                        ALTO_GUISANTE,
                        this
                );

            } else {

                // Representación provisional si no se encuentra el PNG.
                g.setColor(guisante.esCongelante()
                        ? Color.CYAN
                        : Color.GREEN);

                g.fillOval(
                        x,
                        y,
                        ANCHO_GUISANTE,
                        ALTO_GUISANTE
                );
            }
        }
    }


    @Override
    protected void paintChildren(Graphics g) {

        // Dibujar primero los componentes Swing.
        super.paintChildren(g);
        dibujarPodadoras(g);

        if (panelJardin == null) {
            return;
        }

        Graphics gJardin = g.create();

        try {

            // Limitar el dibujo al área del jardín.
            gJardin.clipRect(
                    panelJardin.getX(),
                    panelJardin.getY(),
                    panelJardin.getWidth(),
                    panelJardin.getHeight()
            );

            // Dibujar zombis registrados en el modelo.
            if (tablero != null) {

                int filas = tablero.getFilas();

                if (filas > 0) {

                    int altoFila =
                            panelJardin.getHeight() / filas;

                    for (int i = 0;
                         i < tablero.getListaZombis().getTamaño();
                         i++) {

                        Zombi zombi =
                                tablero.getListaZombis().obtener(i);

                        if (zombi == null || !zombi.estaVivo()) {
                            continue;
                        }

                        int x = panelJardin.getX()
                                + (int) zombi.getPosicionX();

                        int y = panelJardin.getY()
                                + zombi.getFila() * altoFila
                                + (altoFila - 70) / 2;

                        // Elegir la imagen según la clase del zombi.
                        Image imagen = obtenerImagenZombi(zombi);
                        if (imagen != null && imagen.getWidth(this) > 0) {
                            gJardin.drawImage(imagen, x, y, 50, 70, this);
                        } else {
                            gJardin.setColor(Color.GRAY);
                            gJardin.fillOval(x, y, 40, 55);
                        }
                    }
                }
            }

            dibujarGuisantes(gJardin);

            // Dibujar cerebros flotantes.
            for (int i = 0; i < listaCerebros.length; i++) {

                Cerebro cerebro = listaCerebros[i];

                if (cerebro == null
                        || !cerebro.isActivo()
                        || cerebro.haExpirado()) {

                    continue;
                }

                if (imagenCerebro != null
                        && imagenCerebro.getWidth(this) > 0) {

                    gJardin.drawImage(
                            imagenCerebro,
                            cerebro.getX(),
                            cerebro.getY(),
                            40,
                            40,
                            this
                    );

                } else {

                    gJardin.setColor(Color.PINK);

                    gJardin.fillOval(
                            cerebro.getX(),
                            cerebro.getY(),
                            40,
                            40
                    );
                }
            }

        } finally {
            gJardin.dispose();
        }
    }

    private Image obtenerImagenZombi(Zombi zombi) {

        if (zombi instanceof ZombiCaraCono) {
            return imagenZombiCaraCono;
        }

        if (zombi instanceof ZombiSaltador) {
            return imagenZombiSaltador;
        }

        if (zombi instanceof ZombiDeportista) {
            return imagenZombiDeportista;
        }

        if (zombi instanceof ZombiBailon) {
            return imagenZombiBailon;
        }

        if (zombi instanceof Zombistein) {
            return imagenZombistein;
        }

        return imagenZombiNormal;
    }

    private void detenerGeneradorCerebros() {

        if (hiloGeneradorCerebros != null) {
            hiloGeneradorCerebros.interrupt();
        }
    }
}
