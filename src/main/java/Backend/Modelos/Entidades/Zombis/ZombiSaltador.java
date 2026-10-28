package Backend.Modelos.Entidades.Zombis;

public class ZombiSaltador extends Zombi {

    private boolean tieneGarrocha;

    public ZombiSaltador(int fila, double posicionX) {
        super(120, 15, 100, 1.7, fila, posicionX);
        tieneGarrocha = true;
    }

    @Override
    public void avanzar() {
        double velocidadBase = 1.5;

        double multiplicador = tieneGarrocha ? 1.7 : 1.0;

        posicionX += velocidadBase * multiplicador;
    }

    public boolean tieneGarrocha() {
        return tieneGarrocha;
    }

    public void perderGarrocha() {
        tieneGarrocha = false;
    }
}