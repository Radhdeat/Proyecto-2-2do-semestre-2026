package Backend.Modelo.Entidades.Plantas;

public class Patatapum extends Planta {

    // atributos
    private boolean activa;
    private long tiempoCreacion;

    private static final int TIEMPO_PREPARACION = 10;
    private static final int DAÑO_EXPLOSION = 100;

    // constuctor
    public Patatapum(int fila, int columna) {

        super(50, fila, columna);

        this.activa = false;
        // tiempro en segundos
        this.tiempoCreacion = System.currentTimeMillis();
    }

    // ataca de maneta distinta
    @Override
    public void actuar() {

        if (activa || !estaViva()) {
            return;
        }

        long tiempoActual = System.currentTimeMillis();

        long tiempoTranscurrido = tiempoActual - tiempoCreacion;

        if (tiempoTranscurrido >= TIEMPO_PREPARACION * 1000L) {

            activa = true;

            System.out.println("Patatapum activada en fila " + (fila + 1) + ", columna " + (columna + 1));
        }
    }

    public boolean isActiva() {
        return activa;
    }

    public int getDanioExplosion() {
        return DAÑO_EXPLOSION;
    }
}