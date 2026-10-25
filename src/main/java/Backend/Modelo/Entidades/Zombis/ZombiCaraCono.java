package Backend.Modelo.Entidades.Zombis;

public class ZombiCaraCono extends Zombi {
    private int saludEscudo; // 100 de escudo por el cono[cite: 7]

    public ZombiCaraCono(int fila, double posicionX) {
        super(100, 20, 100, 1.0, fila, posicionX); // 100 base, 20 daño, 100 cerebros, velocidad 1[cite: 7]
        this.saludEscudo = 100;
    }

    @Override
    public void avanzar() {
        // Lógica de avance estándar con escudo
    }

    @Override
    public void recibirDanio(int cantidad) {
        if (saludEscudo > 0) {
            saludEscudo -= cantidad;
            if (saludEscudo < 0) {
                // El excedente de daño pasa a la salud base
                int excedente = Math.abs(saludEscudo);
                saludEscudo = 0;
                super.recibirDanio(excedente);
            }
        } else {
            super.recibirDanio(cantidad); // Continúa con mecánicas de zombi común[cite: 7]
        }
    }

    public int getSaludEscudo() { return saludEscudo; }
}