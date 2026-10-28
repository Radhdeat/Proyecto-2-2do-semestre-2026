package Backend.Modelos.Entidades.Zombis;

public class Zombidito extends Zombi {

    public Zombidito(int fila, double posicionX) {
        super(50, 15, 0, 1.7, fila, posicionX);
    }

    @Override
    public void avanzar() {
        posicionX += 2.55 * modificadorVelocidad;
    }
}