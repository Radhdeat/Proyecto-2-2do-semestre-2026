package Backend.Modelo.Entidades.Plantas;

public class Bipetidora extends Planta {
    private int danioProyectil = 15;

    public Bipetidora(int fila, int columna) {
        super(100, fila, columna); // 100 puntos de salud[cite: 5]
    }

    @Override
    public void actuar() {
        // Cabeza frontal dispara hacia la entrada y cabeza trasera dispara hacia atrás[cite: 5]
    }

    public int getDanioProyectil() { return danioProyectil; }
}