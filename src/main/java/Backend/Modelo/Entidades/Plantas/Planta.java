package Backend.Modelo.Entidades.Plantas;

public abstract class Planta {
    protected int salud;
    protected int saludMaxima;
    protected int fila;
    protected int columna;

    public Planta(int salud, int fila, int columna) {
        this.salud = salud;
        this.saludMaxima = salud;
        this.fila = fila;
        this.columna = columna;
    }

    public abstract void actuar();

    public void recibirDanio(int cantidad) {
        this.salud -= cantidad;
        if (this.salud < 0) {
            this.salud = 0;
        }
    }

    public boolean estaViva() {
        return this.salud > 0;
    }

    // Getters y setters explícitos (Sin Lombok)
    public int getSalud() { return salud; }
    public int getSaludMaxima() { return saludMaxima; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
}