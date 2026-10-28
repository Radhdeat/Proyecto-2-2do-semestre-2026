package Backend.Modelos.Entidades.Zombis;

public class ZombiDeportista extends Zombi {

    private int resistenciaCasco;

    public ZombiDeportista(int fila, double posicionX) {
        super(100, 10, 150, 2.0, fila, posicionX);
        resistenciaCasco = 150;
    }

    @Override
    public void avanzar() {
        posicionX += 3.0 * modificadorVelocidad;
    }

    @Override
    public void recibirDaño(int daño) {

        if (daño <= 0) {
            return;
        }

        if (resistenciaCasco > 0) {

            int dañoCasco = Math.min(resistenciaCasco, daño);

            resistenciaCasco -= dañoCasco;
            daño -= dañoCasco;
        }

        if (daño > 0) {
            super.recibirDaño(daño);
        }
    }

    public boolean tieneCasco() {
        return resistenciaCasco > 0;
    }

    public int getResistenciaCasco() {
        return resistenciaCasco;
    }
}