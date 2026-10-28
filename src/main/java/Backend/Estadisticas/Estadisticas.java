package Backend.Estadisticas;

public class Estadisticas {
    private final String jugador;
    private int partidasJugadas, partidasGanadas, partidasPerdidas;
    private int plantasDestruidas, zombisInvocados, cerebrosGastados, podadorasUsadas;
    private int mejorTiempoSegundos = -1;

    public Estadisticas(String jugador) { this.jugador = jugador; }
    public String getJugador() { return jugador; }
    public int getPartidasJugadas() { return partidasJugadas; }
    public int getPartidasGanadas() { return partidasGanadas; }
    public int getPartidasPerdidas() { return partidasPerdidas; }
    public int getPlantasDestruidas() { return plantasDestruidas; }
    public int getZombisInvocados() { return zombisInvocados; }
    public int getCerebrosGastados() { return cerebrosGastados; }
    public int getPodadorasUsadas() { return podadorasUsadas; }
    public int getMejorTiempoSegundos() { return mejorTiempoSegundos; }

    public void registrarResultado(boolean victoria, int tiempo, int plantas, int zombis,
                                   int cerebros, int podadoras) {
        partidasJugadas++;
        if (victoria) {
            partidasGanadas++;
            if (mejorTiempoSegundos < 0 || tiempo < mejorTiempoSegundos) {
                mejorTiempoSegundos = Math.max(0, tiempo);
            }
        } else partidasPerdidas++;
        plantasDestruidas += Math.max(0, plantas);
        zombisInvocados += Math.max(0, zombis);
        cerebrosGastados += Math.max(0, cerebros);
        podadorasUsadas += Math.max(0, podadoras);
    }

    // Lectura exclusiva desde el archivo binario.
    void restaurar(int jugadas, int ganadas, int perdidas, int plantas, int zombis,
                   int cerebros, int mejorTiempo, int podadoras) {
        if (jugadas < 0 || ganadas < 0 || perdidas < 0 || ganadas + perdidas != jugadas
                || plantas < 0 || zombis < 0 || cerebros < 0 || podadoras < 0 || mejorTiempo < -1) {
            throw new IllegalArgumentException("Estadísticas inválidas");
        }
        partidasJugadas = jugadas; partidasGanadas = ganadas; partidasPerdidas = perdidas;
        plantasDestruidas = plantas; zombisInvocados = zombis;
        cerebrosGastados = cerebros; mejorTiempoSegundos = mejorTiempo;
        podadorasUsadas = podadoras;
    }
}
