package Backend.Modelos.Entidades.Zombis;

public class ZombiComun extends Zombi {

    private static final double VELOCIDAD_BASE = 1.5;

    public ZombiComun(int fila, double posicionX) {
        super(100, 15, 50, 1.0, fila, posicionX);
    }

    @Override
    public void avanzar() {
        posicionX += VELOCIDAD_BASE * modificadorVelocidad;
    }

    public boolean haPerdidoBrazo() {
        return salud <= (saludMaxima / 2);
    }
}