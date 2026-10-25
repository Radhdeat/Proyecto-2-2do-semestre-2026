package Backend.Modelo.Entidades.Zombis;

public class ZombiComun extends Zombi {

    public ZombiComun(int fila, double posicionX) {
        super(100, 15, 50, 1.0, fila, posicionX); // 100 salud, 15 daño, 50 cerebros, velocidad 1
    }

    @Override
    public void avanzar() {
        // Lógica de movimiento horizontal considerando el modificador de velocidad
    }

    public boolean haPerdidoBrazo() {
        return this.salud <= (this.saludMaxima / 2); // Al 50% pierde el brazo gráficamente
    }
}