package Backend.Modelos.Entidades.Guisantes;

public class Guisantes {

    // atributos
    private double posicionX;
    private int fila;
    private int daño;
    private double velocidad;
    private int direccion;
    private boolean activo;
    private boolean congelante;

    // constructor
    public Guisantes(double posicionX, int fila, int daño,
                     double velocidad, int direccion,
                     boolean congelante) {

        this.posicionX = posicionX;
        this.fila = fila;
        this.daño = daño;
        this.velocidad = velocidad;
        this.direccion = direccion;
        this.congelante = congelante;
        this.activo = true;
    }

    public void avanzar() {

        if (!activo) {
            return;
        }

        posicionX += velocidad * direccion;
    }

    public void desactivar() {
            activo = false;
        }


    public double getPosicionX() {
        return posicionX;
    }

    public int getFila() {
            return fila;
        }


    public int getDaño() {
        return daño;
    }

    public double getVelocidad() {
        return velocidad;
    }

    public int getDireccion() {
            return direccion;
        }

    public boolean estaActivo() {
            return activo;
        }

    public boolean esCongelante() {
            return congelante;
        }
}

