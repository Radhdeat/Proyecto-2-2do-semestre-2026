package Backend.Modelo.Entidades.Plantas;

public class Hielaguisantes extends Planta {
    private int danioProyectil = 15;
    private double cadencia = 1.5;

    public Hielaguisantes(int fila, int columna) {
        super(100, fila, columna); // 100 puntos de salud[cite: 4]
    }

    @Override
    public void actuar() {
        // Lanza proyectiles congelantes que restan HP y ralentizan al zombi un 40%[cite: 4]
    }

    public int getDanioProyectil() { return danioProyectil; }
}