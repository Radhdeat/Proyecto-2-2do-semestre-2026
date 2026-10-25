package Backend.Modelo.Entidades.Zombis;

public abstract class Zombi {
    protected int salud;
    protected int saludMaxima;
    protected int danio;
    protected int costoCerebros;
    protected double modificadorVelocidad;
    protected int fila;
    protected double posicionX; // Posición horizontal en el carril

    public Zombi(int salud, int danio, int costoCerebros, double modificadorVelocidad, int fila, double posicionX) {
        this.salud = salud;
        this.saludMaxima = salud;
        this.danio = danio;
        this.costoCerebros = costoCerebros;
        this.modificadorVelocidad = modificadorVelocidad;
        this.fila = fila;
        this.posicionX = posicionX;
    }

    public abstract void avanzar();

    public void recibirDanio(int cantidad) {
        this.salud -= cantidad;
        if (this.salud < 0) {
            this.salud = 0;
        }
    }

    public boolean estaVivo() {
        return this.salud > 0;
    }

    // Getters y Setters explícitos (Sin Lombok)
    public int getSalud() { return salud; }
    public int getDanio() { return danio; }
    public int getCostoCerebros() { return costoCerebros; }
    public double getModificadorVelocidad() { return modificadorVelocidad; }
    public int getFila() { return fila; }
    public double getPosicionX() { return posicionX; }
    public void setPosicionX(double posicionX) { this.posicionX = posicionX; }

    public boolean isEstaVivo() {
        return false;
    }
}