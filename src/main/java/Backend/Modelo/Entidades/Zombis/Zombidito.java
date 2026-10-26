package Backend.Modelo.Entidades.Zombis;

public class Zombidito extends Zombi {

    public Zombidito(int fila) {
        super(50, 15, 0, 1.7, fila, 0.0);
    }

    @Override
    public void avanzar() {
        posicionX += 0.5 * modificadorVelocidad;
    }
}
