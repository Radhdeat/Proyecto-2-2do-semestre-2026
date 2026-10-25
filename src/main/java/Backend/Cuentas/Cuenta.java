package Backend.Cuentas;

// Se crean las cuentas
public class Cuenta {

    // Atributo identificador del usuario
    private String nombrePartida;

    // Estadísticas
    private int partidasJugadas;
    private int partidasGanadas;
    private int partidasPerdidas;
    private int plantasDestruidas;
    private int zombisInvocados;
    private int cerebrosGastados;
    private int mejorTiempoVictoria; // Almacenado en segundos
    private int podadorasActivadas;

    // Constructor para una cuenta nueva
    public Cuenta(String nombrePartida) {
        // instanciamos todo como 0 ya que es una nueva partida
        this.nombrePartida = nombrePartida;
        this.partidasJugadas = 0;
        this.partidasGanadas = 0;
        this.partidasPerdidas = 0;
        this.plantasDestruidas = 0;
        this.zombisInvocados = 0;
        this.cerebrosGastados = 0;
        this.mejorTiempoVictoria = 0;
        this.podadorasActivadas = 0;
    }

    // Métodos para calcular las estadisticas de victoria
    public double getPorcentajeVictorias() {
        if (partidasJugadas == 0) return 0.0;
        return ((double) partidasGanadas / partidasJugadas) * 100.0;
    }

    // Metodo para Saber cuantas plantas hemos destruido
    public double getPromedioPlantasDestruidas() {
        if (partidasJugadas == 0) return 0.0;
        return (double) plantasDestruidas / partidasJugadas;
    }

    //getter y seters

    public String getNombrePartida() {
        return nombrePartida;
    }

    public void setNombrePartida(String nombrePartida) {
        this.nombrePartida = nombrePartida;
    }

    public int getPartidasJugadas() {
        return partidasJugadas;
    }

    public void setPartidasJugadas(int partidasJugadas) {
        this.partidasJugadas = partidasJugadas;
    }

    public int getPartidasGanadas() {
        return partidasGanadas;
    }

    public void setPartidasGanadas(int partidasGanadas) {
        this.partidasGanadas = partidasGanadas;
    }

    public int getPartidasPerdidas() {
        return partidasPerdidas;
    }

    public void setPartidasPerdidas(int partidasPerdidas) {
        this.partidasPerdidas = partidasPerdidas;
    }

    public int getPlantasDestruidas() {
        return plantasDestruidas;
    }

    public void setPlantasDestruidas(int plantasDestruidas) {
        this.plantasDestruidas = plantasDestruidas;
    }

    public int getZombisInvocados() {
        return zombisInvocados;
    }

    public void setZombisInvocados(int zombisInvocados) {
        this.zombisInvocados = zombisInvocados;
    }

    public int getCerebrosGastados() {
        return cerebrosGastados;
    }

    public void setCerebrosGastados(int cerebrosGastados) {
        this.cerebrosGastados = cerebrosGastados;
    }

    public int getMejorTiempoVictoria() {
        return mejorTiempoVictoria;
    }

    public void setMejorTiempoVictoria(int mejorTiempoVictoria) {
        this.mejorTiempoVictoria = mejorTiempoVictoria;
    }

    public int getPodadorasActivadas() {
        return podadorasActivadas;
    }

    public void setPodadorasActivadas(int podadorasActivadas) {
        this.podadorasActivadas = podadorasActivadas;
    }
}