package Backend.Modelo.Entidades.Zombis;

public class ZombiSaltador extends Zombi {
    private boolean tieneGarrocha;

    public ZombiSaltador(int fila, double posicionX) {
        super(120, 15, 100, 1.7, fila, posicionX); // 120 salud, 15 daño, 100 cerebros, velocidad 1.7[cite: 7]
        this.tieneGarrocha = true;
    }

    @Override
    public void avanzar() {
        // Lógica de salto al encontrar una planta (si no es Nuez)
    }

    public void saltarPlanta() {
        if (tieneGarrocha) {
            this.tieneGarrocha = false;
            this.modificadorVelocidad = 1.0; // Su velocidad se reduce a 1 para el resto de la partida[cite: 7]
        }
    }

    public boolean isTieneGarrocha() { return tieneGarrocha; }
}
