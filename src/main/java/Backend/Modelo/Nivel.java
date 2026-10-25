package Backend.Modelo;

import Backend.Extructuras.ListaDinamica;

public class Nivel {

    // declaran los atrivutos
    private int idNivel;
    private int solesIniciales;
    private int cantidadOleadas;
    private ListaDinamica<String> tiposZombisPermitidos;
    private ListaDinamica<String> tiposPlantasDisponibles;

    // constructor
    public Nivel() {
        this.tiposZombisPermitidos = new ListaDinamica<>();
        this.tiposPlantasDisponibles = new ListaDinamica<>();
    }

    // Getters y Setters
    public int getIdNivel() { return idNivel; }
    public void setIdNivel(int idNivel) { this.idNivel = idNivel; }

    public int getSolesIniciales() { return solesIniciales; }
    public void setSolesIniciales(int solesIniciales) { this.solesIniciales = solesIniciales; }

    public int getCantidadOleadas() { return cantidadOleadas; }
    public void setCantidadOleadas(int cantidadOleadas) { this.cantidadOleadas = cantidadOleadas; }

    public ListaDinamica<String> getTiposZombisPermitidos() { return tiposZombisPermitidos; }
    public ListaDinamica<String> getTiposPlantasDisponibles() { return tiposPlantasDisponibles; }
}