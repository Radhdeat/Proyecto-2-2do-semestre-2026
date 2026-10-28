package Backend.Modelos.Entidades.Zombis;

public class ZombiCaraCono extends Zombi {

    private int resistenciaCono;

    public ZombiCaraCono(int fila, double posicionX) {
        super(100, 20, 100, 1.0, fila, posicionX);
        resistenciaCono = 100;
    }

    @Override
    public void avanzar() {
        posicionX += 1.5 * modificadorVelocidad;
    }

    @Override
    public void recibirDaño(int daño) {

        if (daño <= 0) {
            return;
        }

        if (resistenciaCono > 0) {

            int dañoCono = Math.min(resistenciaCono, daño);

            resistenciaCono -= dañoCono;
            daño -= dañoCono;
        }

        if (daño > 0) {
            super.recibirDaño(daño);
        }
    }

    public boolean tieneCono() {
        return resistenciaCono > 0;
    }

    public int getResistenciaCono() {
        return resistenciaCono;
    }
}