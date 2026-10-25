package Backend.Modelo.Entidades;

public abstract class EntidadJuego {

    protected int vida;
    protected int vidaMaxima;
    protected int fila;
    protected int columna;
    protected boolean estaViva;

    public EntidadJuego(int vidaMaxima, int fila, int columna) {
        this.vidaMaxima = vidaMaxima;
        this.vida = vidaMaxima;
        this.fila = fila;
        this.columna = columna;
        this.estaViva = true;
    }

    public void recibirDano(int cantidad) {
        this.vida -= cantidad;
        if (this.vida <= 0) {
            this.vida = 0;
            this.estaViva = false;
        }
    }

    // Getters y Setters
    public int getVida() { return vida; }
    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }
    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }
    public boolean isEstaViva() { return estaViva; }
}