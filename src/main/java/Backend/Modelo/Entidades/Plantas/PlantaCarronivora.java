package Backend.Modelo.Entidades.Plantas;

public class PlantaCarronivora extends Planta {
    private boolean digiriendo;
    private int tiempoDigestion;
    private int danioZombistein = 200;

    public PlantaCarronivora(int fila, int columna) {
        super(100, fila, columna); // 100 puntos de salud[cite: 5]
        this.digiriendo = false;
        this.tiempoDigestion = 8; // Tarda 8 segundos en tragárselo[cite: 5]
    }

    @Override
    public void actuar() {
        if (digiriendo) {
            tiempoDigestion--;
            if (tiempoDigestion <= 0) {
                digiriendo = false;
            }
        }
        // Engulle zombis enteros en su casilla o al frente[cite: 5]
    }

    public boolean isDigiriendo() { return digiriendo; }
}