package Backend.Modelo.Entidades.Zombis;

public class Zombistein extends Zombi {

    private boolean zombiditoLanzado;

    public Zombistein(int fila, double posicionX) {

        super(300, 900, 350, 0.7, fila, posicionX);
        this.zombiditoLanzado = false;
    }

    @Override
    public void avanzar() {
        posicionX += 0.5 * modificadorVelocidad;


        if (getSalud() <= (getSalud() / 2) && !zombiditoLanzado) {
            lanzarZombidito();
        }
    }

    public Zombidito lanzarZombidito() {
        this.zombiditoLanzado = true;
        return new Zombidito(getFila());
    }

    public boolean isZombiditoLanzado() {
        return zombiditoLanzado;
    }
}