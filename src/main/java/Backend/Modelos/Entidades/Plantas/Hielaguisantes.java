package Backend.Modelos.Entidades.Plantas;

public class Hielaguisantes extends Planta {

    // atributos
    private int dañoProyectil = 15;
    private double cadencia = 1.5;

    public Hielaguisantes(int fila, int columna) {
        super(100, fila, columna);
    }

    @Override
    public void actuar() {
    }


    public int getDañoProyectil() { return dañoProyectil; }
}