package Backend.Modelo.Entidades.Zombis;

public class Zombidito extends Zombi {

    public Zombidito(int fila) {
        super(50, 15, 0, 1.7, fila, 0.0); // 50 salud, velocidad 1.7, no es invocable por el jugador (costo 0)[cite: 9]
    }

    @Override
    public void avanzar() {
        // Zombidito infiltrándose en las últimas casillas tras ser lanzado[cite: 9]
    }
}
