package Backend.Modelo.Entidades.Zombis;

public class ZombiDeportista extends Zombi {
    private int saludEscudo;

    public ZombiDeportista(int fila, double posicionX) {
        super(100, 10, 150, 2.0, fila, posicionX);
        this.saludEscudo = 150;
    }

    @Override
    public void avanzar() {
        posicionX += 0.5 * modificadorVelocidad;
    }

    @Override
    public void recibirDaño(int cantidad) {
        if (saludEscudo > 0) {
            saludEscudo -= cantidad;
            if (saludEscudo < 0) {
                int excedente = Math.abs(saludEscudo);
                saludEscudo = 0;
                super.recibirDaño(excedente);
            }
        } else {
            super.recibirDaño(cantidad);
        }
    }

    public int getSaludEscudo() { return saludEscudo; }
}