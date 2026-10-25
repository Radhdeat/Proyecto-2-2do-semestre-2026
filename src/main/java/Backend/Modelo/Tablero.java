package Backend.Modelo;

import Backend.Extructuras.ListaDinamica;
import Backend.Modelo.Entidades.Plantas.Planta;
import Backend.Modelo.Entidades.Zombis.Zombi;

public class Tablero {

    // Eliminamos las constantes y usamos variables de instancia
    private int filas;
    private int columnas;

    private Planta[][] casillas;
    private ListaDinamica<Zombi> listaZombis;
    private boolean[] podadorasActivas; // 1 podadora por fila

    // Modificamos el constructor para recibir las dimensiones que vengan del JSON
    public Tablero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.casillas = new Planta[filas][columnas];
        this.listaZombis = new ListaDinamica<>();
        this.podadorasActivas = new boolean[filas];

        // Todas las podadoras inician disponibles
        for (int i = 0; i < filas; i++) {
            podadorasActivas[i] = true;
        }
    }

    // Coloca una planta en la casilla seleccionada si está libre
    public boolean sembrarPlanta(Planta planta) {
        int f = planta.getFila();
        int c = planta.getColumna();

        if (esPosicionValida(f, c) && casillas[f][c] == null) {
            casillas[f][c] = planta;
            return true;
        }
        return false;
    }

    // Elimina una planta destruida o removida por la pala
    public void removerPlanta(int fila, int columna) {
        if (esPosicionValida(fila, columna)) {
            casillas[fila][columna] = null;
        }
    }

    // Comprueba si hay una planta sembrada en la coordenada
    public boolean hayPlantaEn(int fila, int columna) {
        return esPosicionValida(fila, columna) && casillas[fila][columna] != null;
    }

    // Retorna la planta en esa coordenada
    public Planta getPlanta(int fila, int columna) {
        if (esPosicionValida(fila, columna)) {
            return casillas[fila][columna];
        }
        return null;
    }

    // Añade un zombi al jardín (invocado por las oleadas)
    public void agregarZombi(Zombi zombi) {
        if (zombi != null) {
            listaZombis.agregar(zombi);
        }
    }

    // Elimina zombis derrotados de la lista dinámica
    public void limpiarZombisMuertos() {
        for (int i = listaZombis.getTamaño() - 1; i >= 0; i--) {
            // Se cambia .isEstaViva() por .estaViva()
            if (!listaZombis.obtener(i).estaVivo()) {
                listaZombis.eliminar(i);
            }
        }
    }

    private boolean esPosicionValida(int fila, int columna) {
        return fila >= 0 && fila < this.filas && columna >= 0 && columna < this.columnas;
    }

    // Getters originales
    public Planta[][] getCasillas() { return casillas; }
    public ListaDinamica<Zombi> getListaZombis() { return listaZombis; }
    public boolean[] getPodadorasActivas() { return podadorasActivas; }

    // NUEVOS Getters para que PanelPartida pueda acceder a las dimensiones
    public int getFilas() { return filas; }
    public int getColumnas() { return columnas; }
}