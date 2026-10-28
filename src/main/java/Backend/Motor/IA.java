package Backend.Motor;

import Backend.Modelos.Tablero;
import Backend.Modelos.Entidades.Plantas.*;
import Backend.Modelos.Entidades.Zombis.Zombi;

/** IA sin Collections ni hilos adicionales; se ejecuta desde MotorJuego. */
public class IA {
    private final Tablero tablero;
    private final String dificultad;
    private long ultimaSiembra;
    private static final double ANCHO_JARDIN = 755.0;

    public IA(Tablero tablero, String dificultad) {
        this.tablero = tablero;
        this.dificultad = dificultad == null ? "FACIL" : dificultad.trim().toUpperCase();
        this.ultimaSiembra = System.currentTimeMillis();
    }

    public void actualizar() {
        long intervalo;
        if (dificultad.equals("MEDIO")) {
            intervalo = 10000L;
        } else if (dificultad.equals("DIFICIL") || dificultad.equals("DIFÍCIL")) {
            intervalo = 5000L;
        } else {
            return; // FACIL: no agrega plantas
        }
        long ahora = System.currentTimeMillis();
        if (ahora - ultimaSiembra < intervalo) return;
        ultimaSiembra = ahora;
        if (dificultad.equals("MEDIO")) sembrarAleatoriaCercaCasa();
        else sembrarDefensa();
    }

    private void sembrarAleatoriaCercaCasa() {
        int filas = tablero.getFilas();
        int columnas = tablero.getColumnas();
        if (filas <= 0 || columnas <= 0) return;
        // Derecha = casa. Recorre las columnas cercanas a la casa primero.
        int filaInicial = (int)(Math.random() * filas);
        for (int col = columnas - 1; col >= 0; col--) {
            for (int f = 0; f < filas; f++) {
                int fila = (filaInicial + f) % filas;
                if (tablero.getPlanta(fila, col) == null) {
                    int tipo = (int)(Math.random() * 7);
                    tablero.sembrarPlanta(crearPlanta(tipo, fila, col));
                    return;
                }
            }
        }
    }

    private void sembrarDefensa() {
        int filas = tablero.getFilas();
        int columnas = tablero.getColumnas();
        if (filas <= 0 || columnas <= 0) return;
        int filaPeligrosa = -1;
        double zombiMasAvanzado = -1;
        for (int i = 0; i < tablero.getListaZombis().getTamaño(); i++) {
            Zombi z = tablero.getListaZombis().obtener(i);
            if (z != null && z.estaVivo() && z.getFila() >= 0 && z.getFila() < filas
                    && z.getPosicionX() > zombiMasAvanzado) {
                filaPeligrosa = z.getFila();
                zombiMasAvanzado = z.getPosicionX();
            }
        }
        if (filaPeligrosa < 0) { sembrarAleatoriaCercaCasa(); return; }
        int columnaAmenaza = (int)(zombiMasAvanzado / (ANCHO_JARDIN / columnas));
        if (columnaAmenaza >= columnas) columnaAmenaza = columnas - 1;
        // Primero defiende delante del zombi (hacia la casa), si la casilla es libre.
        for (int col = columnaAmenaza + 1; col < columnas; col++) {
            if (tablero.getPlanta(filaPeligrosa, col) == null) {
                Planta planta = zombiMasAvanzado > ANCHO_JARDIN * 0.65
                        ? new Nuez(filaPeligrosa, col)
                        : new Hielaguisantes(filaPeligrosa, col);
                tablero.sembrarPlanta(planta);
                return;
            }
        }
        // Si no hay casillas por delante, refuerza el carril desde la derecha.
        for (int col = columnas - 1; col >= 0; col--) {
            if (tablero.getPlanta(filaPeligrosa, col) == null) {
                tablero.sembrarPlanta(new Nuez(filaPeligrosa, col));
                return;
            }
        }
        sembrarAleatoriaCercaCasa();
    }

    private Planta crearPlanta(int tipo, int fila, int columna) {
        switch(tipo) {
            case 0: return new Lanzaguisantes(fila, columna);
            case 1: return new Hielaguisantes(fila, columna);
            case 2: return new Nuez(fila, columna);
            case 3: return new Patatapum(fila, columna);
            case 4: return new PlantaCarronivora(fila, columna);
            case 5: return new PinchoHierba(fila, columna);
            default: return new Bipetidora(fila, columna);
        }
    }
}
