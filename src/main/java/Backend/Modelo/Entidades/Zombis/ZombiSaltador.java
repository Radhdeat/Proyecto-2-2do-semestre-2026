package Backend.Modelo.Entidades.Zombis;

public class ZombiSaltador extends Zombi {
    private boolean tieneGarrocha;

    public ZombiSaltador(int fila, double posicionX) {
        super(120, 15, 100, 1.7, fila, posicionX);
        this.tieneGarrocha = true;
    }

    @Override
    public void avanzar() {
        posicionX += 0.5 * modificadorVelocidad;
    }

    public void saltarPlanta() {
        if (tieneGarrocha) {
            this.tieneGarrocha = false;
            this.modificadorVelocidad = 1.0;
        }
    }

    public boolean isTieneGarrocha() { return tieneGarrocha; }
}
