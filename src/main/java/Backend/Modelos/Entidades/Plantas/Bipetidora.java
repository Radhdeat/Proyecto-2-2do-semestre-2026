package Backend.Modelos.Entidades.Plantas;

// clase heredada
public class Bipetidora extends Planta {

    private int dañoProyectil = 15;

    public Bipetidora(int fila, int columna) {
        super(100, fila, columna);
    }

    @Override
    public void actuar() {
    }

    public int getDañoProyectil() { return dañoProyectil; }

}