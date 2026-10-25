package Backend.Modelo.Entidades.Plantas;

public class Lanzaguisantes extends Planta {

    private int cadenciaDisparo = 1;
    private int danioProyectil = 20;

    public Lanzaguisantes(int fila, int columna) {
        super(100, fila, columna); // 100 puntos de salud
    }

    @Override
    public void actuar() {
        // Evalúa si existe al menos un zombi en su carril para lanzar un guisante horizontal
    }

    public int getDanioProyectil() { return danioProyectil; }
}
