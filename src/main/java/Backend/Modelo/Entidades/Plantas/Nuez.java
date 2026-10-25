package Backend.Modelo.Entidades.Plantas;

public class Nuez extends Planta {

    public Nuez(int fila, int columna) {
        super(400, fila, columna); // 400 puntos de salud[cite: 4]
    }

    @Override
    public void actuar() {
        // No ataca ni se mueve, solo bloquea el paso[cite: 4]
    }

    public String obtenerAparienciaVisual() {
        double porcentaje = ((double) salud / saludMaxima) * 100.0;
        if (porcentaje >= 66.0) {
            return "INTACTA"; // Salud entre 100 y 66%
        } else if (porcentaje >= 35.0) {
            return "AGRIETADA"; // Salud entre 65 y 35%
        } else {
            return "MUY_DANADA"; // Salud < 35%
        }
    }
}