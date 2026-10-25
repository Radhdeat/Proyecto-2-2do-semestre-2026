package Backend.Modelo.Entidades.Zombis;

public class Zombistein extends Zombi {

    private boolean zombiditoLanzado;

    public Zombistein(int fila, double posicionX) {
        // Parametros: salud, danio, costoCerebros, modificadorVelocidad, fila, posicionX[cite: 8]
        super(300, 0, 350, 0.7, fila, posicionX);
        this.zombiditoLanzado = false;
    }

    @Override
    public void avanzar() {
        // Al llegar al 50% de su vida arroja al Zombidito[cite: 8, 9]
        if (getSalud() <= (getSalud() / 2) && !zombiditoLanzado) {
            lanzarZombidito();
        }
    }

    public Zombidito lanzarZombidito() {
        this.zombiditoLanzado = true;
        // Retorna un Zombidito para ser ubicado cerca de la penúltima casilla del carril[cite: 8, 9]
        return new Zombidito(getFila());
    }

    public boolean isZombiditoLanzado() {
        return zombiditoLanzado;
    }
}