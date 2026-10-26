package Backend.Modelo.Entidades.Zombis;

public class ZombiBailon extends Zombi {

    private int contadorTiempoBaile = 0;

    public ZombiBailon(int fila, double posicionX) {
        super(150, 15, 150, 0.8, fila, posicionX);
    }

    @Override
    public void avanzar() {
        posicionX += 0.5 * modificadorVelocidad;
    }

    public void invocarBailarines() {

    }
}