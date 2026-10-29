package Backend.Estadisticas;

public class Estadisticas {


    private final String jugador;
    private int partidasJugadas, partidasGanadas, partidasPerdidas;
    private int plantasDestruidas, zombisInvocados, cerebrosGastados, podadorasUsadas;
    private int mejorTiempoSegundos = -1;

    public Estadisticas(String jugador) { this.jugador = jugador; }

    // Getter ya que devuelvel la informacion almacenada
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
        // Aumenta el contador
        partidasJugadas++;
        // si victoria
        if (victoria) {
            partidasGanadas++;
            // Si el mejor tiempo es mayor a cero o si el mejor tiempo es mejor al tiempo
            if (mejorTiempoSegundos < 0 || tiempo < mejorTiempoSegundos) {

                // evita que hayan numero negatios
                mejorTiempoSegundos = Math.max(0, tiempo);
            }
        } else partidasPerdidas++;
        //Suma al total de plantas destruidas la cantidad correspondiente a la partida actual
        plantasDestruidas += Math.max(0, plantas);
        //Suma al total de Zombis Invocados la cantidad correspondiente a la partida actual
        zombisInvocados += Math.max(0, zombis);
        //Suma al total de cerebros Gastados la cantidad correspondiente a la partida actual
        cerebrosGastados += Math.max(0, cerebros);
        //Suma al total de Podadoras usadas la cantidad correspondiente a la partida actual
        podadorasUsadas += Math.max(0, podadoras);
    }

    // Lectura exclusiva desde el archivo binario.
    void restaurar(int jugadas, int ganadas, int perdidas, int plantas, int zombis,
                   int cerebros, int mejorTiempo, int podadoras) {
        // si todo es mayor a 0
        if (jugadas < 0 || ganadas < 0 || perdidas < 0 || ganadas + perdidas != jugadas
                || plantas < 0 || zombis < 0 || cerebros < 0 || podadoras < 0 || mejorTiempo < -1) {
            // entonces las estadisticas son invalidas
            throw new IllegalArgumentException("Estadísticas inválidas");
        }
        // comparamos las varaibles
        partidasJugadas = jugadas; partidasGanadas = ganadas; partidasPerdidas = perdidas;
        plantasDestruidas = plantas; zombisInvocados = zombis;
        cerebrosGastados = cerebros; mejorTiempoSegundos = mejorTiempo;
        podadorasUsadas = podadoras;
    }
}
