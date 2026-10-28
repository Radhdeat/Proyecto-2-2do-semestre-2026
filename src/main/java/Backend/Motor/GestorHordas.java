package Backend.Motor;

public class GestorHordas {

    private final int duracionTotal;
    private int tiempoRestante;
    private int bloqueActual;

    public GestorHordas(int duracionTotal) {

        this.duracionTotal = Math.max(1, duracionTotal);
        this.tiempoRestante = this.duracionTotal;
        this.bloqueActual = 1;
    }

    public void actualizar(int segundosRestantes) {

        tiempoRestante = Math.max(0, segundosRestantes);

        int transcurrido = duracionTotal - tiempoRestante;

        // La partida se divide en siete partes iguales.
        // Utilizamos long para evitar desbordamientos.
        int bloque = (int) (
                ((long) transcurrido * 7) / duracionTotal
        ) + 1;

        if (bloque > 7) {
            bloque = 7;
        }

        bloqueActual = bloque;
    }

    public int getBloqueActual() {
        return bloqueActual;
    }

    public boolean esPrimeraHorda() {
        return bloqueActual == 4;
    }

    public boolean esHordaFinal() {
        return bloqueActual == 7;
    }

    public boolean hayHorda() {
        return esPrimeraHorda() || esHordaFinal();
    }

    public int calcularCosto(int costoOriginal) {

        if (costoOriginal <= 0) {
            return 0;
        }

        if (esHordaFinal()) {
            // Descuento del 75 %.
            // Se cobra el 25 %, redondeado hacia arriba.
            return (int) (((long) costoOriginal + 3) / 4);
        }

        if (esPrimeraHorda()) {
            // Descuento del 50 %.
            return (costoOriginal + 1) / 2;
        }

        return costoOriginal;
    }

    public String getDescripcion() {

        if (esPrimeraHorda()) {
            return "¡PRIMERA HORDA! -50 %";
        }

        if (esHordaFinal()) {
            return "¡HORDA FINAL! -75 %";
        }

        return "Bloque " + bloqueActual + " de 7";
    }
}