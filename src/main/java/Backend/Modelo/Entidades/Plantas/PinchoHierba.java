package Backend.Modelo.Entidades.Plantas;

public class PinchoHierba extends Planta {
    private int danio = 10;

    public PinchoHierba(int fila, int columna) {
        super(Integer.MAX_VALUE, fila, columna); // Inmune a zombis normales, sin puntos de salud estándar[cite: 5]
    }

    @Override
    public void actuar() {
        // Infringe daño a los zombis que caminan sobre su casilla[cite: 5]
    }

    @Override
    public void recibirDanio(int cantidad) {
        // Solo puede ser destruida al ser aplastada por un Zombistein[cite: 5]
    }
}