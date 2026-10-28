package Backend.Modelos.Entidades.Plantas;

public class Lanzaguisantes extends Planta {

    private int cadenciaDisparo = 1;
    private int dañoProyectil = 20;

    public Lanzaguisantes(int fila, int columna) {
        super(100, fila, columna);
    }

    @Override
    public void actuar() {
    }

    public int getDañoProyectil() { return dañoProyectil; }
}
