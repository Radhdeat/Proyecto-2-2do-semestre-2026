package Backend.Modelo.Entidades.Plantas;

public class Patatapum extends Planta {
    private boolean activa;
    private int segundosTranscurridos;
    private final int tiempoPreparacion = 10;
    private int danioExplosion = 100;

    public Patatapum(int fila, int columna) {
        super(50, fila, columna); // 50 puntos de salud[cite: 4]
        this.activa = false;
        this.segundosTranscurridos = 0;
    }

    @Override
    public void actuar() {
        if (!activa) {
            segundosTranscurridos++;
            if (segundosTranscurridos >= tiempoPreparacion) {
                activa = true; // Tiempo de preparación de 10 segundos cumplido[cite: 4]
            }
        }
    }

    public boolean isActiva() { return activa; }
    public int getDanioExplosion() { return danioExplosion; }
}