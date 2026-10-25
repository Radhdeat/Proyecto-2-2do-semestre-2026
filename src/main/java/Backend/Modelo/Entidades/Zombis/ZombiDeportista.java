package Backend.Modelo.Entidades.Zombis;

public class ZombiDeportista extends Zombi {
    private int saludEscudo; // 150 de escudo por el casco

    public ZombiDeportista(int fila, double posicionX) {
        super(100, 10, 150, 2.0, fila, posicionX); // 100 base, 10 daño, 150 cerebros, velocidad 2.0[cite: 8]
        this.saludEscudo = 150;
    }

    @Override
    public void avanzar() {
        // Lógica de movimiento a velocidad 2.0
    }

    @Override
    public void recibirDanio(int cantidad) {
        if (saludEscudo > 0) {
            saludEscudo -= cantidad;
            if (saludEscudo < 0) {
                int excedente = Math.abs(saludEscudo);
                saludEscudo = 0;
                super.recibirDanio(excedente);
            }
        } else {
            super.recibirDanio(cantidad);
        }
    }

    public int getSaludEscudo() { return saludEscudo; }
}