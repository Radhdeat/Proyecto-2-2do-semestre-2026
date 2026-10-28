package Frontend.Jardin;

import Backend.Modelos.Entidades.Zombis.*;
import Backend.Modelos.Tablero;
import Backend.Motor.MotorJuego;
import Frontend.Menu.MenuInicio;
import Frontend.Menu.VentanaPrincipal;
import Backend.Motor.GestorHordas;
import Backend.Guardado.Cargar;
import Backend.Guardado.Guardar;
import Backend.Estadisticas.ManejadorEstadisticas;
import java.io.IOException;
import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;

import Backend.Modelos.Entidades.Plantas.*;
import Backend.Modelos.Entidades.Guisantes.Guisantes;

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
    private String jugadorActual;
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss_SSS");
    private int zombisInvocados = 0;
    private int cerebrosGastadosEnPartida = 0;
    private int podadorasUsadasAlCargar = 0;
    // Estadísticas anteriores a una carga.
    private int plantasDestruidasPrevias = 0;
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
    private static final int TAMANO_CEREBRO = 40;

    private boolean partidaPausada = false;
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
    private Image imagenZombiSinBrazo;
    private Image imagenZombiSinCono;
    private Image imagenZombiSinCasco;
    private Image imagenZombiSinGarrocha;
    private Image imagenZombidito;
    private Image imagenZombiBailarin;
    private Image imagenZombiCaraCono;
    private Image imagenZombiSaltador;
    private Image imagenZombiDeportista;
    private Image imagenZombiBailon;
    private Image imagenZombistein;

    private Image imagenPodadora;

    private Image imagenGuisante;
    private Image imagenGuisanteCongelado;

    private GestorHordas gestorHordas;
    private JLabel lblHorda;
    private int ultimoBloqueMostrado = 1;

    private static final int ANCHO_ZOMBI = 65;
    private static final int ALTO_ZOMBI = 85;
    private static final int ANCHO_PODADORA = 55;
    private static final int ALTO_PODADORA = 55;

    private static final int ANCHO_GUISANTE = 25;
    private static final int ALTO_GUISANTE = 25;


    // Mantiene compatibilidad con el constructor antiguo; antes de guardar
    // debes vincular el jugador mediante establecerJugadorActual(...).
    public PanelPartida(VentanaPrincipal ventana, String[][] jardinDatos,
                        int duracionSegundos, int cerebrosIniciales, String dificultad) {
        this(ventana, jardinDatos, duracionSegundos, cerebrosIniciales, dificultad, ventana.getCuentaActual());
    }

    // Usa este constructor desde el menú cuando la cuenta ya está seleccionada.
    public PanelPartida(VentanaPrincipal ventana, String[][] jardinDatos,
                        int duracionSegundos, int cerebrosIniciales, String dificultad,
                        String jugadorActual) {
        this.jugadorActual = (jugadorActual == null || jugadorActual.trim().isEmpty())
                ? ventana.getCuentaActual() : jugadorActual;

        this.ventana = ventana;
        this.segundosRestantes = Math.max(0, duracionSegundos);
        this.duracionInicial = this.segundosRestantes;
        this.gestorHordas = new GestorHordas(duracionInicial);
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

        imagenZombiNormal = new ImageIcon("src/main/resources/Zombis/normal.gif").getImage();

        imagenZombiCaraCono = new ImageIcon("src/main/resources/Zombis/cono.png").getImage();

        imagenZombiSaltador = new ImageIcon("src/main/resources/Zombis/saltador.png").getImage();

        imagenZombiDeportista = new ImageIcon("src/main/resources/Zombis/deportista.png").getImage();

        imagenZombiBailon = new ImageIcon("src/main/resources/Zombis/bailon.png").getImage();

        imagenZombistein = new ImageIcon("src/main/resources/Zombis/zombistein.png").getImage();

        imagenPodadora = new ImageIcon("src/main/resources/Recursos/Podadora.png").getImage();

        imagenGuisante = new ImageIcon("src/main/resources/Recursos/guisante.png").getImage();

        imagenGuisanteCongelado = new ImageIcon("src/main/resources/Recursos/guisanteCongelado.png").getImage();

        imagenZombiSinBrazo = new ImageIcon("src/main/resources/Zombis/normalSin.gif").getImage();

        imagenZombiSinCono = cargarImagenOpcional("src/main/resources/Zombis/sincono.gif", imagenZombiNormal);

        imagenZombiSinCasco = cargarImagenOpcional("src/main/resources/Zombis/sincasco.png", imagenZombiDeportista);

        imagenZombiSinGarrocha = cargarImagenOpcional("src/main/resources/Zombis/saltadorSin.png", imagenZombiSaltador);

        imagenZombidito = cargarImagenOpcional("src/main/resources/Zombis/zombidito.png", imagenZombiNormal);

        imagenZombiBailarin = cargarImagenOpcional("src/main/resources/Zombis/bailarin.png", imagenZombiBailon);

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
        motor.setEventoPlantaDestruida((fila, columna) ->
                soltarCerebrosPorPlanta(fila, columna));
        motor.configurarInteligencia(dificultadJuego);
        // Las plantas se activan desde el inicio, incluso si aún no invocas zombis.
        motor.iniciarJuego();




        iniciarCronometroNativo();
        iniciarGeneradoresCerebros();
        configurarAtajoGuardado();
        configurarAtajoCarga();

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

                if (!juegoActivo || partidaPausada) {
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

            if (!juegoActivo) {
                return;
            }

            if (!partidaPausada) {
                pausarPartida();
            }
            mostrarMenuPausa();
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

        lblHorda = new JLabel(
                gestorHordas.getDescripcion(),
                SwingConstants.CENTER
        );

        lblHorda.setFont(new Font("Arial", Font.BOLD, 14));
        lblHorda.setForeground(Color.RED);

        lblHorda.setBounds(540, 65, 300, 25);

        add(lblHorda);
    }


    private void pausarPartida() {

        if (!juegoActivo || partidaPausada) {
            return;
        }

        partidaPausada = true;

        motor.pausarJuego();

        System.out.println("PARTIDA PAUSADA");

        repaint();
    }

    private void reanudarPartida() {

        if (!juegoActivo || !partidaPausada) {
            return;
        }

        partidaPausada = false;

        motor.reanudarJuego();

        System.out.println("PARTIDA REANUDADA");

        repaint();
    }




    /** Nombre de la cuenta seleccionada. Se configura desde el menú. */
    public void establecerJugadorActual(String jugador) {
        this.jugadorActual = jugador;
    }

    private static File carpetaDePartidas(String jugador) throws IOException {
        if (jugador == null || !jugador.matches("[\\p{L}\\p{N} _-]{1,64}")
                || jugador.trim().isEmpty()) {
            throw new IOException("No hay una cuenta válida seleccionada. Vincula el nickname al crear PanelPartida.");
        }
        File carpeta = new File(new File("cuentas", jugador.trim()), "partidas");
        if (!carpeta.isDirectory() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear carpeta del jugador: " + carpeta);
        }
        return carpeta;
    }

    private void mostrarMenuPausa() {
        if (!juegoActivo || !partidaPausada) return;
        Object[] opciones = {"Reanudar", "Guardar y salir"};
        int opcion = JOptionPane.showOptionDialog(this,
                "La partida está pausada. Selecciona una opción:",
                "Partida pausada", JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);
        if (opcion == 1) {
            guardarYSalir();
        } else {
            // Cerrar la ventana también retoma la partida.
            reanudarPartida();
        }
    }

    private void guardarYSalir() {
        if (!juegoActivo || !partidaPausada) return;
        File archivo = guardarPartidaEnCuenta();
        if (archivo == null) return; // No abandonar la partida si falló el guardado.
        juegoActivo = false;
        detenerGeneradorCerebros();
        if (hiloCronometro != null) hiloCronometro.interrupt();
        if (motor != null) motor.detenerJuego();
        JOptionPane.showMessageDialog(this,
                "Guardada: " + archivo.getName() + "\nCuenta: " + jugadorActual,
                "Guardado correcto", JOptionPane.INFORMATION_MESSAGE);
        ventana.cambiarPantalla(new MenuInicio(ventana));
    }

    /** Selecciona y carga una partida del jugador activo. */
    public static boolean cargarDesdeMenu(VentanaPrincipal ventana, String jugador) {
        try {
            File archivo = elegirPartidaGuardada(null, jugador);
            if (archivo == null) return false;
            return cargarArchivoDesdeMenu(ventana, jugador, archivo);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(ventana, "Error cargando partida: " + ex.getMessage(),
                    "Carga fallida", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    /** Recibe el archivo concreto seleccionado en PanelCargarPartida.
     *  No abre otra ventana y no crea una partida nueva desde el JSON.
     */
    public static boolean cargarArchivoDesdeMenu(VentanaPrincipal ventana,
                                                 String jugador, File archivo) {
        PanelPartida restaurada = null;
        String cuentaAnterior = ventana.getCuentaActual();
        try {
            if (jugador == null || jugador.trim().isEmpty()) {
                throw new IOException("La partida no tiene una cuenta propietaria válida.");
            }
            File carpeta = carpetaDePartidas(jugador).getCanonicalFile();
            File validado = archivo.getCanonicalFile();
            if (!validado.isFile()
                    || !carpeta.equals(validado.getParentFile())
                    || !validado.getName().toLowerCase().endsWith(".dat")) {
                throw new IOException("El archivo no pertenece a la cuenta actual.");
            }
            Cargar.DatosPartida datos = new Cargar().leer(validado.getPath());
            // Vincular la sesión al propietario del archivo, incluso tras reiniciar la aplicación.
            ventana.setCuentaActual(jugador);
            // El constructor de reconstruirPartida utiliza la ventana y la cuenta.
            // Este objeto auxiliar no debe iniciar motores ni cronómetros.
            // Construimos el restaurador mediante un panel inicial vacío,
            // y detenemos sus procesos antes de mostrar la partida recuperada.
            if (datos.plantas == null || datos.plantas.length == 0
                    || datos.plantas[0] == null || datos.plantas[0].length == 0) {
                throw new IOException("El archivo no contiene un jardín válido.");
            }
            String[][] jardin = new String[datos.plantas.length][datos.plantas[0].length];
            for (int f = 0; f < jardin.length; f++) {
                for (int c = 0; c < jardin[f].length; c++) jardin[f][c] = "VACIO";
            }
            PanelPartida auxiliar = new PanelPartida(ventana, jardin,
                    datos.duracionInicial, datos.cerebrosActuales, datos.dificultad, jugador);
            try {
                auxiliar.pausarPartida();
                restaurada = auxiliar.reconstruirPartida(datos);
                restaurada.pausarPartida();
            } finally {
                auxiliar.juegoActivo = false;
                auxiliar.detenerGeneradorCerebros();
                if (auxiliar.hiloCronometro != null) auxiliar.hiloCronometro.interrupt();
                if (auxiliar.motor != null) auxiliar.motor.detenerJuego();
            }
            ventana.cambiarPantalla(restaurada);
            restaurada.reanudarPartida();
            return true;
        } catch (IOException | IllegalArgumentException ex) {
            ventana.setCuentaActual(cuentaAnterior);
            if (restaurada != null) {
                restaurada.juegoActivo = false;
                restaurada.detenerGeneradorCerebros();
                if (restaurada.hiloCronometro != null) restaurada.hiloCronometro.interrupt();
                if (restaurada.motor != null) restaurada.motor.detenerJuego();
            }
            JOptionPane.showMessageDialog(ventana, "Error cargando partida: " + ex.getMessage(),
                    "Carga fallida", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private static File elegirPartidaGuardada(java.awt.Component padre, String jugador)
            throws IOException {
        File carpeta = carpetaDePartidas(jugador);
        JFileChooser selector = new JFileChooser(carpeta);
        selector.setDialogTitle("Selecciona una partida de " + jugador);
        selector.setFileFilter(new FileNameExtensionFilter("Partidas binarias (*.dat)", "dat"));
        if (selector.showOpenDialog(padre) != JFileChooser.APPROVE_OPTION) return null;
        File elegido = selector.getSelectedFile().getCanonicalFile();
        if (!elegido.isFile()
                || !elegido.getParentFile().equals(carpeta.getCanonicalFile())
                || !elegido.getName().endsWith(".dat")) {
            throw new IOException("Selecciona un archivo .dat de la cuenta actual.");
        }
        return elegido;
    }

    /** Ctrl+S guarda únicamente si el juego está pausado. */
    private void configurarAtajoGuardado() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("control S"), "guardarPartida");
        getActionMap().put("guardarPartida", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarPartidaBinaria();
            }
        });
    }

    /** Ctrl+L reconstruye una partida guardada en versión 1. */
    private void configurarAtajoCarga() {
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                KeyStroke.getKeyStroke("control L"), "cargarPartida");
        getActionMap().put("cargarPartida", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarPartidaBinaria();
            }
        });
    }

    private void cargarPartidaBinaria() {
        if (!juegoActivo || !partidaPausada) {
            JOptionPane.showMessageDialog(this, "Primero pausa el juego.");
            return;
        }
        try {
            File archivo = elegirPartidaGuardada(this, jugadorActual);
            if (archivo == null) return;
            Cargar.DatosPartida datos = new Cargar().leer(archivo.getPath());
            PanelPartida nueva = reconstruirPartida(datos);
            nueva.establecerJugadorActual(jugadorActual);
            nueva.pausarPartida();
            juegoActivo = false;
            detenerGeneradorCerebros();
            if (hiloCronometro != null) hiloCronometro.interrupt();
            if (motor != null) motor.detenerJuego();
            ventana.cambiarPantalla(nueva);
            nueva.reanudarPartida();
        } catch (IOException | IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar: " + ex.getMessage(),
                    "Carga fallida", JOptionPane.ERROR_MESSAGE);
        }
    }

    private PanelPartida reconstruirPartida(Cargar.DatosPartida d)
            throws IOException {
        int filas = d.plantas.length;
        int columnas = d.plantas[0].length;
        if (d.podadorasActivas.length != filas) {
            throw new IOException("No coincide la cantidad de podadoras y filas.");
        }
        if (d.cerebrosX.length > MAX_CEREBROS) {
            throw new IOException("Demasiados cerebros visibles en el archivo.");
        }
        String[][] jardin = new String[filas][columnas];
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Cargar.PlantaGuardada pg = d.plantas[f][c];
                jardin[f][c] = pg == null ? "VACIO" : tipoPlantaParaJson(pg.tipo);
            }
        }
        // Crear el panel y sus componentes sin modificar la partida actual.
        PanelPartida nueva = new PanelPartida(ventana, jardin,
                d.duracionInicial, d.cerebrosActuales, d.dificultad, jugadorActual);
        nueva.segundosRestantes = d.segundosRestantes;
        nueva.duracionInicial = d.duracionInicial;
        nueva.zombisInvocados = d.zombisInvocados;
        // El guardado V1 no incluye cerebros gastados; no se pueden reconstruir retrospectivamente.
        nueva.plantasDestruidasPrevias = d.plantasDestruidas;
        nueva.lblTiempo.setText(nueva.formatearTiempo(d.segundosRestantes));
        nueva.gestorHordas.actualizar(d.segundosRestantes);
        nueva.ultimoBloqueMostrado = nueva.gestorHordas.getBloqueActual();
        nueva.lblHorda.setText(nueva.gestorHordas.getDescripcion());

        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                Cargar.PlantaGuardada pg = d.plantas[f][c];
                if (pg == null) continue;
                Planta planta = nueva.tablero.getPlanta(f,c);
                if (planta == null) throw new IOException("Planta no construida: " + pg.tipo);
                if (pg.salud <= 0 || pg.salud > planta.getSaludMaxima()) {
                    throw new IOException("Salud inválida de planta: " + pg.tipo);
                }
                planta.recibirDaño(planta.getSalud() - pg.salud);
            }
        }

        for (Cargar.ZombiGuardado zg : d.zombis) {
            if (zg == null) continue;
            if (zg.fila < 0 || zg.fila >= filas || zg.salud <= 0
                    || !Double.isFinite(zg.posicionX)) {
                throw new IOException("Zombi inválido en el guardado.");
            }
            Zombi z = construirZombiGuardado(zg);
            // La versión 1 solo persiste salud total; la protección especial
            // no está disponible para restaurarla fielmente.
            if (zg.salud < z.getSalud()) {
                z.recibirDaño(z.getSalud() - zg.salud);
            }
            nueva.tablero.agregarZombi(z);
        }

        for (Cargar.GuisanteGuardado gg : d.guisantes) {
            if (gg == null) continue;
            if (gg.fila < 0 || gg.fila >= filas || !Double.isFinite(gg.posicionX)) {
                throw new IOException("Guisante inválido en el guardado.");
            }
            nueva.tablero.agregarGuisante(new Guisantes(
                    gg.posicionX, gg.fila, gg.dano, 6.0,
                    gg.direccion, gg.congelante));
        }
        boolean[] activas = nueva.tablero.getPodadorasActivas();
        boolean[] moviendo = nueva.tablero.getPodadorasEnMovimiento();
        double[] posiciones = nueva.tablero.getPosicionesPodadoras();
        for (int f = 0; f < filas; f++) {
            activas[f] = d.podadorasActivas[f];
            moviendo[f] = d.podadorasEnMovimiento[f];
            posiciones[f] = d.posicionesPodadoras[f];
        }
        // No contar de nuevo las podadoras consumidas antes del guardado.
        for (int f = 0; f < filas; f++) {
            if (!activas[f]) nueva.podadorasUsadasAlCargar++;
        }
        for (int i = 0; i < d.cerebrosX.length; i++) {
            nueva.listaCerebros[i] = new Cerebro(d.cerebrosX[i], d.cerebrosY[i]);
        }
        nueva.panelJardin.actualizarPlantas(nueva.tablero.getCasillas());
        nueva.repaint();
        return nueva;
    }

    private String tipoPlantaParaJson(String tipo) throws IOException {
        switch (tipo) {
            case "Lanzaguisantes": return "LANZAGUISANTES";
            case "Hielaguisantes": return "HIELAGUISANTES";
            case "Nuez": return "NUEZ";
            case "Patatapum": return "PATATAPUM";
            case "PlantaCarronivora": return "CARRONIVORA";
            case "PinchoHierba": return "PINCHOHIERBA";
            case "Bipetidora": return "BIPETIDORA";
            default: throw new IOException("Tipo de planta desconocido: " + tipo);
        }
    }

    private Zombi construirZombiGuardado(Cargar.ZombiGuardado z)
            throws IOException {
        switch (z.tipo) {
            case "ZombiComun": return new ZombiComun(z.fila, z.posicionX);
            case "ZombiCaraCono": return new ZombiCaraCono(z.fila, z.posicionX);
            case "ZombiSaltador": return new ZombiSaltador(z.fila, z.posicionX);
            case "ZombiDeportista": return new ZombiDeportista(z.fila, z.posicionX);
            case "ZombiBailon": return new ZombiBailon(z.fila, z.posicionX);
            case "ZombiBailarin": return new ZombiBailarin(z.fila, z.posicionX);
            case "Zombistein": return new Zombistein(z.fila, z.posicionX);
            case "Zombidito": return new Zombidito(z.fila, z.posicionX);
            default: throw new IOException("Tipo de zombi desconocido: " + z.tipo);
        }
    }

    private void guardarPartidaBinaria() {
        if (!juegoActivo || !partidaPausada) {
            JOptionPane.showMessageDialog(this, "Primero pausa la partida.");
            return;
        }
        File archivo = guardarPartidaEnCuenta();
        if (archivo != null) {
            JOptionPane.showMessageDialog(this, "Partida guardada en:\n" + archivo);
        }
    }

    private File guardarPartidaEnCuenta() {
        int cantidad = 0;
        for (int i = 0; i < listaCerebros.length; i++) {
            Cerebro b = listaCerebros[i];
            if (b != null && b.isActivo() && !b.haExpirado()) cantidad++;
        }
        int[] posicionesX = new int[cantidad];
        int[] posicionesY = new int[cantidad];
        int indice = 0;
        for (int i = 0; i < listaCerebros.length; i++) {
            Cerebro b = listaCerebros[i];
            if (b != null && b.isActivo() && !b.haExpirado()) {
                posicionesX[indice] = b.getX();
                posicionesY[indice] = b.getY();
                indice++;
            }
        }
        try {
            String nombrePartida = JOptionPane.showInputDialog(
                    this,
                    "Ingresa un nombre para la partida:",
                    "Guardar partida",
                    JOptionPane.QUESTION_MESSAGE
            );

            if (nombrePartida == null) {
                return null;
            }

            nombrePartida = nombrePartida.trim();

            if (nombrePartida.isEmpty()) {
                JOptionPane.showMessageDialog(
                        this,
                        "Debes ingresar un nombre para la partida."
                );
                return null;
            }

            if (!nombrePartida.matches("[a-zA-Z0-9_-]{1,40}")) {
                JOptionPane.showMessageDialog(
                        this,
                        "Usa solo letras, números, guiones o guion bajo."
                );
                return null;
            }

            // La cuenta es propietaria del guardado; el nombre es solo del archivo.
            String cuenta = ventana.getCuentaActual();
            if (cuenta == null || cuenta.trim().isEmpty()) {
                cuenta = jugadorActual;
            }
            File carpeta = carpetaDePartidas(cuenta);
            jugadorActual = cuenta;

            File archivo = new File(
                    carpeta,
                    nombrePartida + "_"
                            + LocalDateTime.now().format(FORMATO_FECHA)
                            + ".dat"
            );
            new Guardar().guardar(archivo.getPath(), tablero, duracionInicial,
                    segundosRestantes, cerebrosActuales, dificultadJuego,
                    zombisInvocados, plantasDestruidasPrevias + motor.getPlantasDestruidas(),
                    gestorHordas.getBloqueActual(), posicionesX, posicionesY);
            return archivo;
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar: " + ex.getMessage(),
                    "Error de guardado", JOptionPane.ERROR_MESSAGE);
            return null;
        }
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

        if (!juegoActivo || partidaPausada) {
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

        int costoOriginal = obtenerCostoZombi(zombiSeleccionado);

        int costo = gestorHordas.calcularCosto(costoOriginal);

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
        cerebrosGastadosEnPartida += costo;
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
                return 100;

            case "ZombiSaltador":
                return 100;

            case "ZombiDeportista":
                return 150;

            case "ZombiBailon":
                return 150;

            case "Zombistein":
                return 350;

            default:
                return 50;
        }
    }

    // Un solo hilo planifica todos los generadores; las operaciones Swing van al EDT.
    private void iniciarGeneradoresCerebros() {

        int cantidadGeneradores;

        if (dificultadJuego.equalsIgnoreCase("Facil")
                || dificultadJuego.equalsIgnoreCase("Fácil")) {

            cantidadGeneradores = 6;

        } else if (dificultadJuego.equalsIgnoreCase("Dificil")
                || dificultadJuego.equalsIgnoreCase("Difícil")) {

            cantidadGeneradores = 4;

        } else {
            cantidadGeneradores = 5;
        }

        // Los tiempos se cuentan solamente mientras se juega.
        final long[] proximasGeneraciones =
                new long[cantidadGeneradores];

        final long[] tiempoActivo = {0L};

        final long[] ultimoInstante = {
                System.nanoTime()
        };

        for (int i = 0; i < cantidadGeneradores; i++) {
            proximasGeneraciones[i] = intervaloAleatorioCerebro();
        }

        hiloGeneradorCerebros = new Thread(() -> {

            while (!Thread.currentThread().isInterrupted()) {

                try {
                    Thread.sleep(100);

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }

                SwingUtilities.invokeLater(() -> {

                    if (!juegoActivo) {
                        return;
                    }

                    long ahora = System.nanoTime();

                    long diferencia =
                            (ahora - ultimoInstante[0]) / 1000000L;

                    ultimoInstante[0] = ahora;

                    // Mientras está pausado, no avanza el reloj.
                    if (partidaPausada) {
                        return;
                    }

                    tiempoActivo[0] += Math.max(0L, diferencia);

                    for (int i = 0;
                         i < proximasGeneraciones.length;
                         i++) {

                        if (tiempoActivo[0] >= proximasGeneraciones[i]) {

                            int posX = 94
                                    + (int) (Math.random() * 715);

                            int posY = 95
                                    + (int) (Math.random() * 409);

                            agregarCerebroFlotante(posX, posY);

                            proximasGeneraciones[i] =
                                    tiempoActivo[0]
                                            + intervaloAleatorioCerebro();
                        }
                    }
                });
            }
        }, "GeneradoresCerebros");

        hiloGeneradorCerebros.start();
    }



    private long intervaloAleatorioCerebro() {
        return 5000L + (long) (Math.random() * 10001L);
    }

    // Cada planta destruida por un zombi suelta entre 3 y 6 cerebros.
    private void soltarCerebrosPorPlanta(int fila, int columna) {
        if (!juegoActivo || panelJardin == null || tablero == null) return;
        int cantidad = 3 + (int) (Math.random() * 4);
        double anchoCelda = (double) panelJardin.getWidth() / tablero.getColumnas();
        double altoCelda = (double) panelJardin.getHeight() / tablero.getFilas();
        int centroX = panelJardin.getX() + (int) ((columna + 0.5) * anchoCelda);
        int centroY = panelJardin.getY() + (int) ((fila + 0.5) * altoCelda);
        int minX = panelJardin.getX();
        int minY = panelJardin.getY();
        int maxX = minX + panelJardin.getWidth() - TAMANO_CEREBRO;
        int maxY = minY + panelJardin.getHeight() - TAMANO_CEREBRO;
        for (int i = 0; i < cantidad; i++) {
            int x = centroX - TAMANO_CEREBRO / 2 + (int) (Math.random() * 71) - 35;
            int y = centroY - TAMANO_CEREBRO / 2 + (int) (Math.random() * 71) - 35;
            x = Math.max(minX, Math.min(maxX, x));
            y = Math.max(minY, Math.min(maxY, y));
            agregarCerebroFlotante(x, y);
        }
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

        if (!juegoActivo || partidaPausada) {
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

                // Pausar no debe finalizar el hilo del cronómetro.
                if (partidaPausada) {
                    continue;
                }

                SwingUtilities.invokeLater(() -> {
                    // Evitar descontar tiempo si la pausa ocurrió
                    // antes de ejecutarse esta tarea en el EDT.
                    if (!juegoActivo || partidaPausada) {
                        return;
                    }

                    segundosRestantes = Math.max(0, segundosRestantes - 1);

                    gestorHordas.actualizar(segundosRestantes);

                    int nuevoBloque = gestorHordas.getBloqueActual();

                    if (nuevoBloque != ultimoBloqueMostrado) {

                        ultimoBloqueMostrado = nuevoBloque;

                        System.out.println(
                                "Cambio de bloque: " + nuevoBloque
                        );

                        if (gestorHordas.hayHorda()) {
                            System.out.println(
                                    gestorHordas.getDescripcion()
                            );
                        }
                    }

                    lblHorda.setText(gestorHordas.getDescripcion());



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
        int destruidas = plantasDestruidasPrevias + (motor != null ? motor.getPlantasDestruidas() : 0);

        // Registrar una sola vez; guardar y salir no registra una partida terminada.
        int podadorasConsumidas = 0;
        for (boolean activa : tablero.getPodadorasActivas()) {
            if (!activa) podadorasConsumidas++;
        }
        podadorasConsumidas = Math.max(0, podadorasConsumidas - podadorasUsadasAlCargar);


        String cuentaEstadisticas = jugadorActual;

        if (cuentaEstadisticas == null || cuentaEstadisticas.trim().isEmpty()) {
            cuentaEstadisticas = ventana.getCuentaActual();
        }

        if (cuentaEstadisticas == null || cuentaEstadisticas.trim().isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No se encontró la cuenta del jugador. "
                            + "No se guardarán estadísticas con un nombre incorrecto."
            );
            return;
        }

        try {
            new ManejadorEstadisticas().registrar(cuentaEstadisticas, victoria,
                    Math.max(0, transcurrido), destruidas, zombisInvocados,
                    cerebrosGastadosEnPartida, podadorasConsumidas);
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this,
                    "La partida terminó, pero no se pudieron guardar las estadísticas: " + ex.getMessage(),
                    "Error de estadísticas", JOptionPane.ERROR_MESSAGE);
        }

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
                                + (altoFila - ALTO_ZOMBI) / 2;

                        // Elegir la imagen según la clase del zombi.
                        Image imagen = obtenerImagenZombi(zombi);
                        if (imagen != null && imagen.getWidth(this) > 0) {
                            gJardin.drawImage(imagen, x, y, ANCHO_ZOMBI, ALTO_ZOMBI, this);
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

    /** Obtiene la imagen según el tipo y el estado del zombi. */
    private Image obtenerImagenZombi(Zombi zombi) {
        if (zombi instanceof ZombiCaraCono) {
            ZombiCaraCono cono = (ZombiCaraCono) zombi;
            return cono.tieneCono() ? imagenZombiCaraCono : imagenZombiSinCono;
        }

        if (zombi instanceof ZombiDeportista) {
            ZombiDeportista deportista = (ZombiDeportista) zombi;
            return deportista.tieneCasco() ? imagenZombiDeportista : imagenZombiSinCasco;
        }

        if (zombi instanceof ZombiSaltador) {
            ZombiSaltador saltador = (ZombiSaltador) zombi;
            return saltador.tieneGarrocha() ? imagenZombiSaltador : imagenZombiSinGarrocha;
        }

        if (zombi instanceof Zombidito) {
            return imagenZombidito;
        }

        if (zombi instanceof ZombiBailarin) {
            return imagenZombiBailarin;
        }

        if (zombi instanceof ZombiBailon) {
            return imagenZombiBailon;
        }

        if (zombi instanceof Zombistein) {
            return imagenZombistein;
        }

        if (zombi instanceof ZombiComun) {
            ZombiComun comun = (ZombiComun) zombi;
            if (comun.haPerdidoBrazo()
                    && imagenZombiSinBrazo != null
                    && imagenZombiSinBrazo.getWidth(this) > 0) {
                return imagenZombiSinBrazo;
            }
        }

        return imagenZombiNormal;
    }

    /** Imagen opcional: permite arrancar aunque falte algún recurso gráfico. */
    private Image cargarImagenOpcional(String ruta, Image alternativa) {
        ImageIcon icono = new ImageIcon(ruta);
        if (icono.getIconWidth() > 0 && icono.getIconHeight() > 0) {
            return icono.getImage();
        }
        System.err.println("No se encontró la imagen opcional: " + ruta);
        return alternativa;
    }

    private void detenerGeneradorCerebros() {

        if (hiloGeneradorCerebros != null) {
            hiloGeneradorCerebros.interrupt();
        }
    }
}
