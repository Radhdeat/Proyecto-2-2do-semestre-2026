package Backend.Modelos.Entidades.Plantas;

public class PlantaCarronivora extends Planta {

    // atributos
    private boolean digiriendo;
    private long inicioDigestion;

    private static final int TIEMPO_DIGESTION = 8;
    private static final int DAÑO_ZOMBISTEIN = 200;

    public PlantaCarronivora(int fila, int columna) {

        super(100, fila, columna);

        this.digiriendo = false;
        this.inicioDigestion = 0;
    }

    @Override
    public void actuar() {

        if (!estaViva() || !digiriendo) {
            return;
        }

        long tiempoActual = System.currentTimeMillis();

        long tiempoTranscurrido =
                tiempoActual - inicioDigestion;

        if (tiempoTranscurrido >= TIEMPO_DIGESTION * 1000L) {

            digiriendo = false;
            inicioDigestion = 0;

            System.out.println(
                    "Planta carnívora lista para atacar nuevamente."
            );
        }
    }

    public void comenzarDigestion() {

        if (digiriendo || !estaViva()) {
            return;
        }

        digiriendo = true;
        inicioDigestion = System.currentTimeMillis();

        System.out.println(
                "Planta carnívora comenzó a digerir."
        );
    }

    public boolean isDigiriendo() {
        return digiriendo;
    }

    public boolean puedeAtacar() {
        return estaViva() && !digiriendo;
    }

    public int getDañoZombistein() {
        return DAÑO_ZOMBISTEIN;
    }
}