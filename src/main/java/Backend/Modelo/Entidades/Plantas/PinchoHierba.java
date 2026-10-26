package Backend.Modelo.Entidades.Plantas;

public class PinchoHierba extends Planta {

    // ATRIBUTOS
    private int daño = 10;

    public PinchoHierba(int fila, int columna) {
        super(Integer.MAX_VALUE, fila, columna);
    }

    @Override
    public void actuar() {

    }

    @Override
    public void recibirDaño(int cantidad) {
    }
}