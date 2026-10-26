package Backend.Modelo.Entidades.Zombis;

public class ZombiCaraCono extends Zombi {
    private int saludEscudo;

    public ZombiCaraCono(int fila, double posicionX) {
        super(100, 20, 100, 1.0, fila, posicionX);
        this.saludEscudo = 100;
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
                // El excedente de daño pasa a la salud base
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