package Frontend.Jardin;

import Backend.Modelos.Entidades.Plantas.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelJardinTablero extends JPanel {

    public interface FilaClickListener {
        void onFilaClicked(int filaSeleccionada);
    }

    private int filas;
    private int columnas;

    private JButton[][] matrizBotones;
    private Planta[][] matrizPlantas;

    private Image imagenPastoOriginal;
    private FilaClickListener filaClickListener;

    private Image imagenLansaguisantes;
    private Image imagenHielaguisantes;
    private Image imagenNuez;
    private Image imagenPatatapum;
    private Image imagenCarnivora;
    private Image imagenBipetidora;
    private Image imagenBinchoHierba;
    private Image imagenNuezAgrietada;
    private Image imagenNuezMuyDanada;
    private String[][] estadosVisualesNuez;

    private Image imagenPatatapumInactiva;
    private boolean[][] estadosVisualesPatatapum;

    public PanelJardinTablero(
            String[][] jsonJardin,
            FilaClickListener listener) {

        if (jsonJardin == null
                || jsonJardin.length == 0
                || jsonJardin[0] == null
                || jsonJardin[0].length == 0) {

            throw new IllegalArgumentException(
                    "El jardín debe tener al menos una fila y una columna."
            );
        }

        this.filas = jsonJardin.length;
        this.columnas = jsonJardin[0].length;
        this.filaClickListener = listener;

        estadosVisualesNuez = new String[filas][columnas];

        setLayout(new GridLayout(filas, columnas, 0, 0));
        setOpaque(false);

        matrizBotones = new JButton[filas][columnas];
        matrizPlantas = new Planta[filas][columnas];

        imagenLansaguisantes = new ImageIcon("src/main/resources/Plantas/guisantes.png").getImage();

        imagenCarnivora = new ImageIcon("src/main/resources/Plantas/carnivora.png").getImage();

        imagenBipetidora = new ImageIcon("src/main/resources/Plantas/bipetidora.png").getImage();

        imagenNuez = new ImageIcon("src/main/resources/Plantas/nuez.png").getImage();

        imagenPatatapum = new ImageIcon("src/main/resources/Plantas/patatapum.png").getImage();

        imagenBinchoHierba = new ImageIcon("src/main/resources/Plantas/pincho.png").getImage();

        imagenHielaguisantes = new ImageIcon("src/main/resources/Plantas/hiela.png").getImage();

        imagenNuezAgrietada = new ImageIcon("src/main/resources/Plantas/nuezDañada.png").getImage();

        imagenNuezMuyDanada = new ImageIcon("src/main/resources/Plantas/nuezMuy.png").getImage();

        imagenPatatapumInactiva = new ImageIcon("src/main/resources/Plantas/inactiva.png").getImage();


        estadosVisualesPatatapum = new boolean[filas][columnas];

        cargarImagenPasto();
        inicializarComponentes(jsonJardin);
    }

    private void cargarImagenPasto() {

        ImageIcon icono = new ImageIcon(
                "src/main/resources/pasto.png"
        );

        if (icono.getIconWidth() > 0) {
            imagenPastoOriginal = icono.getImage();
        } else {
            imagenPastoOriginal = null;
        }
    }

    private void inicializarComponentes(String[][] jsonJardin) {

        for (int i = 0; i < filas; i++) {

            if (jsonJardin[i] == null
                    || jsonJardin[i].length != columnas) {

                throw new IllegalArgumentException(
                        "Todas las filas deben tener la misma cantidad de columnas."
                );
            }

            for (int j = 0; j < columnas; j++) {

                final int filaSeleccionada = i;

                JButton botonCasilla = new JButton() {

                    @Override
                    protected void paintComponent(Graphics g) {

                        if (imagenPastoOriginal != null) {

                            g.drawImage(
                                    imagenPastoOriginal,
                                    0,
                                    0,
                                    getWidth(),
                                    getHeight(),
                                    this
                            );

                        } else {

                            g.setColor(new Color(110, 180, 60));
                            g.fillRect(
                                    0, 0,
                                    getWidth(),
                                    getHeight()
                            );
                        }

                        super.paintComponent(g);
                    }
                };

                botonCasilla.setFocusPainted(false);
                botonCasilla.setBorderPainted(false);
                botonCasilla.setBorder(null);
                botonCasilla.setMargin(new Insets(0, 0, 0, 0));
                botonCasilla.setContentAreaFilled(false);
                botonCasilla.setOpaque(false);

                String tipoPlanta = jsonJardin[i][j];

                if (tipoPlanta != null) {
                    tipoPlanta = tipoPlanta.trim().toUpperCase();
                }

                if (tipoPlanta != null
                        && !tipoPlanta.isEmpty()
                        && !tipoPlanta.equals("VACIO")) {

                    Planta planta = crearInstanciaPlanta(
                            tipoPlanta,
                            i,
                            j
                    );

                    matrizPlantas[i][j] = planta;

                    if (planta != null) {

                        ImageIcon imagenPlanta =
                                cargarImagenPlanta(tipoPlanta);

                        if (imagenPlanta != null) {

                            // Mostrar la imagen dentro de la casilla.
                            botonCasilla.setIcon(imagenPlanta);

                            botonCasilla.setHorizontalAlignment(
                                    SwingConstants.CENTER
                            );

                            botonCasilla.setVerticalAlignment(
                                    SwingConstants.CENTER
                            );

                        } else {

                            // Si no existe el PNG, mostrar las siglas.
                            botonCasilla.setText(
                                    obtenerSiglasPlanta(tipoPlanta)
                            );

                            botonCasilla.setForeground(Color.WHITE);
                            botonCasilla.setFont(
                                    new Font("Arial", Font.BOLD, 12)
                            );
                        }

                    } else {

                        System.err.println(
                                "Tipo de planta desconocido: "
                                        + tipoPlanta
                        );
                    }

                } else {

                    matrizPlantas[i][j] = null;
                    botonCasilla.setText("");
                }

                botonCasilla.addMouseListener(new MouseAdapter() {

                    @Override
                    public void mouseClicked(MouseEvent e) {

                        if (filaClickListener != null) {

                            filaClickListener.onFilaClicked(
                                    filaSeleccionada
                            );
                        }
                    }
                });

                matrizBotones[i][j] = botonCasilla;
                add(botonCasilla);
            }
        }
    }

    private Planta crearInstanciaPlanta(
            String tipo,
            int fila,
            int columna) {

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

    private ImageIcon cargarImagenPlanta(String tipo) {

        Image imagenSeleccionada;

        switch (tipo) {

            case "LANZAGUISANTES":
                imagenSeleccionada = imagenLansaguisantes;
                break;

            case "PATATAPUM":
                imagenSeleccionada = imagenPatatapumInactiva;
                break;

            case "HIELAGUISANTES":
                imagenSeleccionada = imagenHielaguisantes;
                break;

            case "NUEZ":
                imagenSeleccionada = imagenNuez;
                break;

            case "CARRONIVORA":
                imagenSeleccionada = imagenCarnivora;
                break;

            case "PINCHOHIERBA":
                imagenSeleccionada = imagenBinchoHierba;
                break;

            case "BIPETIDORA":
                imagenSeleccionada = imagenBipetidora;
                break;

            default:
                return null;
        }

        // Verificar que la imagen exista.
        if (imagenSeleccionada == null
                || imagenSeleccionada.getWidth(null) <= 0) {

            System.err.println(
                    "No se pudo cargar la imagen de: " + tipo
            );

            return null;
        }

        // Escalar la imagen a 65x65 píxeles.
        Image imagenEscalada = imagenSeleccionada.getScaledInstance(
                65,
                65,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(imagenEscalada);
    }

    private ImageIcon obtenerIconoPatatapum(Patatapum patatapum) {

        Image imagen;

        if (patatapum.isActiva()) {
            imagen = imagenPatatapum;
        } else {
            imagen = imagenPatatapumInactiva;
        }

        if (imagen == null || imagen.getWidth(this) <= 0) {
            return null;
        }

        Image imagenEscalada = imagen.getScaledInstance(
                65,
                65,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(imagenEscalada);
    }


    private ImageIcon obtenerIconoNuez(Nuez nuez) {

        Image imagen;

        String estado = nuez.obtenerAparienciaVisual();

        switch (estado) {

            case "AGRIETADA":
                imagen = imagenNuezAgrietada;
                break;

            case "MUY_DANADA":
                imagen = imagenNuezMuyDanada;
                break;

            case "INTACTA":
                imagen = imagenNuez;
                break;

            default:
                return null;
        }

        if (imagen == null || imagen.getWidth(this) <= 0) {
            return null;
        }

        Image imagenEscalada = imagen.getScaledInstance(
                65,
                65,
                Image.SCALE_SMOOTH
        );

        return new ImageIcon(imagenEscalada);
    }


    private String obtenerSiglasPlanta(String tipo) {

        switch (tipo) {

            case "LANZAGUISANTES":
                return "LNZ";

            case "PATATAPUM":
                return "PTA";

            case "HIELAGUISANTES":
                return "HIE";

            case "NUEZ":
                return "NUZ";

            case "CARRONIVORA":
                return "CAR";

            case "PINCHOHIERBA":
                return "PNC";

            case "BIPETIDORA":
                return "BIP";

            default:
                return "";
        }
    }

    public void actualizarPlantas(Planta[][] plantasActuales) {

        for (int fila = 0; fila < filas; fila++) {

            for (int columna = 0; columna < columnas; columna++) {

                Planta planta = plantasActuales[fila][columna];

                JButton boton = matrizBotones[fila][columna];

                // La planta fue destruida.
                if (planta == null || !planta.estaViva()) {

                    if (matrizPlantas[fila][columna] != null
                            || boton.getIcon() != null) {

                        boton.setIcon(null);
                        boton.setText("");
                    }

                    matrizPlantas[fila][columna] = null;
                    estadosVisualesNuez[fila][columna] = null;
                    estadosVisualesPatatapum[fila][columna] = false;

                    continue;
                }

                boolean esPlantaNueva = matrizPlantas[fila][columna] != planta;
                matrizPlantas[fila][columna] = planta;

                if (planta instanceof Nuez) {

                    Nuez nuez = (Nuez) planta;

                    String estadoActual = nuez.obtenerAparienciaVisual();
                    String estadoAnterior = estadosVisualesNuez[fila][columna];

                    if (!estadoActual.equals(estadoAnterior)) {

                        ImageIcon icono = obtenerIconoNuez(nuez);

                        if (icono != null) {
                            boton.setIcon(icono);
                            boton.setText("");
                        }

                        estadosVisualesNuez[fila][columna] = estadoActual;
                    }

                } else if (planta instanceof Patatapum) {

                    Patatapum patatapum = (Patatapum) planta;

                    boolean estadoActual = patatapum.isActiva();

                    // También actualizar la primera vez que se dibuja.
                    if (esPlantaNueva
                            || estadosVisualesPatatapum[fila][columna] != estadoActual) {

                        ImageIcon icono = obtenerIconoPatatapum(patatapum);

                        if (icono != null) {
                            boton.setIcon(icono);
                            boton.setText("");
                        }

                        estadosVisualesPatatapum[fila][columna] = estadoActual;
                    }

                    estadosVisualesNuez[fila][columna] = null;

                } else {
                    // Las plantas añadidas por la IA deben dibujarse al aparecer.
                    if (esPlantaNueva) {
                        String tipo = obtenerTipoPlanta(planta);
                        ImageIcon icono = cargarImagenPlanta(tipo);
                        boton.setIcon(icono);
                        boton.setText(icono == null ? obtenerSiglasPlanta(tipo) : "");
                    }
                    estadosVisualesNuez[fila][columna] = null;
                }
            }
        }
    }

    private String obtenerTipoPlanta(Planta planta) {
        if (planta instanceof Lanzaguisantes) return "LANZAGUISANTES";
        if (planta instanceof Hielaguisantes) return "HIELAGUISANTES";
        if (planta instanceof PlantaCarronivora) return "CARRONIVORA";
        if (planta instanceof PinchoHierba) return "PINCHOHIERBA";
        if (planta instanceof Bipetidora) return "BIPETIDORA";
        if (planta instanceof Patatapum) return "PATATAPUM";
        if (planta instanceof Nuez) return "NUEZ";
        return "";
    }

    public JButton[][] getMatrizBotones() {
        return matrizBotones;
    }

    public Planta[][] getMatrizPlantas() {
        return matrizPlantas;
    }
}
