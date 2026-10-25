package Backend.Modelo.Entidades.Zombis;

public class ZombiBailon extends Zombi {
    private int contadorTiempoBaile = 0;

    public ZombiBailon(int fila, double posicionX) {
        super(150, 15, 150, 0.8, fila, posicionX); // 150 salud, 15 daño, 150 cerebros, velocidad 0.8[cite: 8]
    }

    @Override
    public void avanzar() {
        // Lógica de avance continuo e interrupción de baile cada 5 segundos[cite: 8]
    }

    public void invocarBailarines() {
        // Genera la invocación en casillas adyacentes[cite: 8]
    }
}