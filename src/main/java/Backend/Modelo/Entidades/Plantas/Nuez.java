package Backend.Modelo.Entidades.Plantas;

public class Nuez extends Planta {

    // constructor
    public Nuez(int fila, int columna) {
        super(400, fila, columna);
    }

    @Override
    public void actuar() {
    }


    public String obtenerAparienciaVisual() {
        double porcentaje = ((double) salud / saludMaxima) * 100.0;
        if (porcentaje >= 66.0) {
            return "INTACTA";
        } else if (porcentaje >= 35.0) {
            return "AGRIETADA";
        } else {
            return "MUY_DANADA";
        }
    }
}