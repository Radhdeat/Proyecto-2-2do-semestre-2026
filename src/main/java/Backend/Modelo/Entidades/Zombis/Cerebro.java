package Backend.Modelo.Entidades.Zombis;

public class Cerebro {
    private int x;
    private int y;
    private long tiempoCreacion;
    private boolean activo;
    private final long TIEMPO_VIDA = 5000; // 5 segundos

    public Cerebro(int x, int y) {
        this.x = x;
        this.y = y;
        this.tiempoCreacion = System.currentTimeMillis();
        this.activo = true;
    }

    public boolean haExpirado() {
        return (System.currentTimeMillis() - tiempoCreacion) > TIEMPO_VIDA;
    }

    public boolean contienePunto(int px, int py) {
        // Asumiendo un tamaño de 40x40 píxeles para el icono del cerebro
        return activo && px >= x && px <= x + 40 && py >= y && py <= y + 40;
    }

    public boolean isActivo() {
        return activo;
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