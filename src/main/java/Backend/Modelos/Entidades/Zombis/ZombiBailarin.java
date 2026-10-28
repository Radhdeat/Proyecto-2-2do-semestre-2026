package Backend.Modelos.Entidades.Zombis;

public class ZombiBailarin extends Zombi {
    public ZombiBailarin(int fila, double posicionX) {
        super(100, 15, 0, 1.0, fila, posicionX);
    }

    @Override
    public void avanzar() {
        posicionX += 1.5 * modificadorVelocidad;
    }
}
