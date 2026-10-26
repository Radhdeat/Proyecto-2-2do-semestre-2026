package Backend.Modelo.Entidades.Zombis;

public class ZombiComun extends Zombi {

    public ZombiComun(int fila, double posicionX) {
        super(100, 15, 50, 1.0, fila, posicionX);
    }

    @Override
    public void avanzar() {
        posicionX += 0.5 * modificadorVelocidad;
    }

    public boolean haPerdidoBrazo() {
        return this.salud <= (this.saludMaxima / 2);
    }
}