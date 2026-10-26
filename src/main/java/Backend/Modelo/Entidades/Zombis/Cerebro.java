package Backend.Modelo.Entidades.Zombis;

public class Cerebro {

    private int x;
    private int y;
    private long tiempoCreacion;
    private boolean activo;

    private static final long TIEMPO_VIDA = 5000L;

    public Cerebro(int x, int y) {
        this.x = x;
        this.y = y;
        this.tiempoCreacion = System.currentTimeMillis();
        this.activo = true;
    }

    public boolean haExpirado() {
        return System.currentTimeMillis() - tiempoCreacion
                >= TIEMPO_VIDA;
    }

    public boolean contienePunto(int px, int py) {
        return activo
                && !haExpirado()
                && px >= x
                && px <= x + 40
                && py >= y
                && py <= y + 40;
    }

    public boolean isActivo() {
        return activo && !haExpirado();
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}