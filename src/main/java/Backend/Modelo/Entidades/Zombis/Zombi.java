package Backend.Modelo.Entidades.Zombis;

public abstract class Zombi {
    protected int salud;
    protected int saludMaxima;
    protected int daño;
    protected int costoCerebros;
    protected double modificadorVelocidad;
    protected int fila;
    protected double posicionX;

    public Zombi(int salud, int daño, int costoCerebros, double modificadorVelocidad, int fila, double posicionX) {
        this.salud = salud;
        this.saludMaxima = salud;
        this.daño = daño;
        this.costoCerebros = costoCerebros;
        this.modificadorVelocidad = modificadorVelocidad;
        this.fila = fila;
        this.posicionX = posicionX;
    }

    public abstract void avanzar();

    public void recibirDaño(int cantidad) {
        this.salud -= cantidad;
        if (this.salud < 0) {
            this.salud = 0;
        }
    }

    public boolean estaVivo() {
        return this.salud > 0;
    }

    public int getSalud() {
        return salud;
    }

    public void ralentizar(double porcentaje) {
        modificadorVelocidad = modificadorVelocidad * (1.0 - porcentaje);
    }
    protected boolean invasionEvaluada = false;

    public boolean isInvasionEvaluada() {
        return invasionEvaluada;
    }

    public void setInvasionEvaluada(boolean invasionEvaluada) {
        this.invasionEvaluada = invasionEvaluada;
    }

    public int getDaño() {
        return daño;
    }

    public int getCostoCerebros() {
        return costoCerebros;
    }

    public double getModificadorVelocidad() {
        return modificadorVelocidad;
    }

    public int getFila() {
        return fila;
    }

    public double getPosicionX() {
        return posicionX;
    }

    public void setPosicionX(double posicionX) {
        this.posicionX = posicionX;
    }

}