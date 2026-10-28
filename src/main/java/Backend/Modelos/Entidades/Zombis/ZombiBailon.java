package Backend.Modelos.Entidades.Zombis;

public class ZombiBailon extends Zombi {

    private long ultimoBaile;

    public ZombiBailon(int fila, double posicionX) {
        super(150, 15, 150, 0.8, fila, posicionX);
        ultimoBaile = System.currentTimeMillis();
    }

    @Override
    public void avanzar() {
        posicionX += 1.2 * modificadorVelocidad;
    }

    public boolean puedeBailar() {
        return System.currentTimeMillis() - ultimoBaile >= 5000L;
    }

    public void registrarBaile() {
        ultimoBaile = System.currentTimeMillis();
    }
}