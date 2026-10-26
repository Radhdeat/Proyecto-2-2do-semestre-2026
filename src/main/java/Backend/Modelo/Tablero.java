package Backend.Modelo;

import Backend.Extructuras.ListaDinamica;
import Backend.Modelo.Entidades.Plantas.Planta;
import Backend.Modelo.Entidades.Zombis.Zombi;
import Backend.Modelo.Entidades.Guisantes.Guisantes;

public class Tablero {

    // ========================================
    //           ATRIBUTOS GENERALES
    // ========================================

    private int filas;
    private int columnas;

    // Plantas
    private Planta[][] casillas;

    // Zombis
    private ListaDinamica<Zombi> listaZombis;

    // Guisantes disparados por las plantas
    private ListaDinamica<Guisantes> listaGuisantes;

    // Podadoras
    private boolean[] podadorasActivas;
    private boolean[] podadorasEnMovimiento;
    private double[] posicionesPodadoras;

    // Dimensiones del jardín
    private static final double ANCHO_JARDIN = 755.0;
    private static final double ANCHO_PODADORA = 55.0;

    // ========================================
    //              CONSTRUCTOR
    // ========================================

    public Tablero(int filas, int columnas) {

        this.filas = filas;
        this.columnas = columnas;

        // Inicializar matriz de plantas
        this.casillas = new Planta[filas][columnas];

        // Inicializar listas dinámicas
        this.listaZombis = new ListaDinamica<>();
        this.listaGuisantes = new ListaDinamica<>();

        // Inicializar podadoras
        this.podadorasActivas = new boolean[filas];
        this.podadorasEnMovimiento = new boolean[filas];
        this.posicionesPodadoras = new double[filas];

        // Todas las podadoras comienzan disponibles
        for (int i = 0; i < filas; i++) {

            podadorasActivas[i] = true;
            podadorasEnMovimiento[i] = false;
            posicionesPodadoras[i] = ANCHO_JARDIN;
        }
    }

    // ========================================
    //              PLANTAS
    // ========================================

    public boolean sembrarPlanta(Planta planta) {

        if (planta == null) {
            return false;
        }

        int fila = planta.getFila();
        int columna = planta.getColumna();

        if (esPosicionValida(fila, columna)
                && casillas[fila][columna] == null) {

            casillas[fila][columna] = planta;
            return true;
        }

        return false;
    }

    public void removerPlanta(int fila, int columna) {

        if (esPosicionValida(fila, columna)) {
            casillas[fila][columna] = null;
        }
    }

    public boolean hayPlantaEn(int fila, int columna) {

        return esPosicionValida(fila, columna)
                && casillas[fila][columna] != null;
    }

    public Planta getPlanta(int fila, int columna) {

        if (esPosicionValida(fila, columna)) {
            return casillas[fila][columna];
        }

        return null;
    }

    // ========================================
    //              ZOMBIS
    // ========================================

    public void agregarZombi(Zombi zombi) {

        if (zombi != null) {
            listaZombis.agregar(zombi);
        }
    }

    public void limpiarZombisMuertos() {

        for (int i = listaZombis.getTamaño() - 1; i >= 0; i--) {

            Zombi zombi = listaZombis.obtener(i);

            if (zombi == null || !zombi.estaVivo()) {
                listaZombis.eliminar(i);
            }
        }
    }

    // ========================================
    //              GUISANTES
    // ========================================

    // Agregar un guisante disparado por una planta
    public void agregarGuisante(Guisantes guisante) {

        if (guisante != null) {
            listaGuisantes.agregar(guisante);
        }
    }

    // Eliminar guisantes que impactaron o salieron del jardín
    public void limpiarGuisantesInactivos() {

        for (int i = listaGuisantes.getTamaño() - 1; i >= 0; i--) {

            Guisantes guisante = listaGuisantes.obtener(i);

            if (guisante == null || !guisante.estaActivo()) {
                listaGuisantes.eliminar(i);
            }
        }
    }

    // ========================================
    //              PODADORAS
    // ========================================

    public void activarPodadora(int fila) {

        if (fila < 0 || fila >= filas) {
            return;
        }

        if (!podadorasActivas[fila]) {
            return;
        }

        podadorasActivas[fila] = false;
        podadorasEnMovimiento[fila] = true;
        posicionesPodadoras[fila] = ANCHO_JARDIN;

        System.out.println(
                "Podadora activada en fila " + (fila + 1)
        );
    }

    public void moverPodadora(int fila, double velocidad) {

        if (fila < 0 || fila >= filas) {
            return;
        }

        if (!podadorasEnMovimiento[fila]) {
            return;
        }

        posicionesPodadoras[fila] -= velocidad;

        if (posicionesPodadoras[fila] <= -ANCHO_PODADORA) {

            podadorasEnMovimiento[fila] = false;

            System.out.println(
                    "Podadora de fila "
                            + (fila + 1)
                            + " terminó su recorrido."
            );
        }
    }

    public boolean podadoraDisponible(int fila) {

        if (fila < 0 || fila >= filas) {
            return false;
        }

        return podadorasActivas[fila];
    }

    public boolean podadoraMoviendose(int fila) {

        if (fila < 0 || fila >= filas) {
            return false;
        }

        return podadorasEnMovimiento[fila];
    }

    // ========================================
    //         VALIDACIÓN DE POSICIONES
    // ========================================

    private boolean esPosicionValida(int fila, int columna) {

        return fila >= 0
                && fila < filas
                && columna >= 0
                && columna < columnas;
    }

    // ========================================
    //                GETTERS
    // ========================================

    public Planta[][] getCasillas() {
        return casillas;
    }

    public ListaDinamica<Zombi> getListaZombis() {
        return listaZombis;
    }

    public ListaDinamica<Guisantes> getListaGuisantes() {
        return listaGuisantes;
    }

    public boolean[] getPodadorasActivas() {
        return podadorasActivas;
    }

    public boolean[] getPodadorasEnMovimiento() {
        return podadorasEnMovimiento;
    }

    public double[] getPosicionesPodadoras() {
        return posicionesPodadoras;
    }

    public int getFilas() {
        return filas;
    }

    public int getColumnas() {
        return columnas;
    }
}