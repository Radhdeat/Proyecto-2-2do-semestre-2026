package Backend.Modelos.Entidades.Plantas;

public abstract class Planta {
    // atributos
    protected int salud;
    protected int saludMaxima;
    protected int fila;
    protected int columna;

    // constructor
    public Planta(int salud, int fila, int columna) {
        this.salud = salud;
        this.saludMaxima = salud;
        this.fila = fila;
        this.columna = columna;
    }

    // abstracto ya que cada una hace algo distinto
    public abstract void actuar();

    // pero todas reciben daño igual
    public void recibirDaño(int cantidad) {
        this.salud -= cantidad;
        if (this.salud < 0) {
            this.salud = 0;
        }
    }

    // se revisa si esta viva
    public boolean estaViva() {
        return this.salud > 0;
    }

    // Getters y setters explícitos (Sin Lombok)
    public int getSalud() { return salud; }
    public int getSaludMaxima() { return saludMaxima; }
    public int getFila() { return fila; }
    public int getColumna() { return columna; }
}