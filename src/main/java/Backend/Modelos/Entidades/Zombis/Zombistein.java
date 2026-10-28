package Backend.Modelos.Entidades.Zombis;

public class Zombistein extends Zombi {

    private boolean zombiditoLanzado;

    public Zombistein(int fila, double posicionX) {
        super(300, 15, 350, 0.7, fila, posicionX);
        zombiditoLanzado = false;
    }

    @Override
    public void avanzar() {
        posicionX += 1.05 * modificadorVelocidad;
    }

    public boolean puedeLanzarZombidito() {

        return !zombiditoLanzado
                && salud > 0
                && salud <= saludMaxima / 2;
    }

    public void registrarLanzamiento() {
        zombiditoLanzado = true;
    }
}