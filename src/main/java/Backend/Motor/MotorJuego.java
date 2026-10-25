package Backend.Motor;

import Backend.Modelo.Tablero;
import Backend.Modelo.Entidades.Zombis.Zombi;

public class MotorJuego implements Runnable {

    private Tablero tablero;
    private boolean juegoActivo;
    private Thread hiloMotor;
    private int contadorCiclos;

    // Interfaz para avisarle a la pantalla (Frontend) que debe redibujarse
    private Runnable actualizadorPantalla;

    public MotorJuego(Tablero tablero, Runnable actualizadorPantalla) {
        this.tablero = tablero;
        this.actualizadorPantalla = actualizadorPantalla;
        this.juegoActivo = false;
        this.contadorCiclos = 0;
    }

    // Inicia el hilo en segundo plano (Requisito del proyecto)
    public void iniciarJuego() {
        if (!juegoActivo) {
            juegoActivo = true;
            hiloMotor = new Thread(this);
            hiloMotor.start();
        }
    }

    public void detenerJuego() {
        this.juegoActivo = false;
    }

    @Override
    public void run() {
        while (juegoActivo) {
            actualizarLogica();

            // Avisar a la interfaz gráfica que vuelva a pintar los gráficos con los nuevos datos
            if (actualizadorPantalla != null) {
                actualizadorPantalla.run();
            }

            // Pausa el hilo unos milisegundos para correr a ~30 FPS (Frames Por Segundo)
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                System.out.println("El hilo del motor fue interrumpido.");
            }
        }
    }

    private void actualizarLogica() {
        contadorCiclos++;

        // 1. Mover todos los zombis vivos hacia la izquierda
        for (int i = 0; i < tablero.getListaZombis().getTamaño(); i++) {
            Zombi z = tablero.getListaZombis().obtener(i);
            if (z.isEstaVivo()) {
                z.avanzar();

                // Si el zombi llega a la posición 0 (o menos), gana o activa podadora
                if (z.getPosicionX() <= 0) {
                    evaluarInvasion(z);
                }
            }
        }

        // 2. Limpiar el tablero de entidades destruidas
        tablero.limpiarZombisMuertos();

        // 3. Cada 50 ciclos (aprox 1.5 segundos), realizar acciones de plantas (disparos/soles)
        if (contadorCiclos % 50 == 0) {
            gestionarPlantas();
        }
    }

    private void evaluarInvasion(Zombi z) {
        int fila = z.getFila();
        if (tablero.getPodadorasActivas()[fila]) {
            // Activar podadora, matar al zombi y consumir la podadora
            z.recibirDanio(9999);
            tablero.getPodadorasActivas()[fila] = false;
            System.out.println("¡Podadora activada en la fila " + fila + "!");
        } else {
            // Si no hay podadora, el zombi entró a la casa. Fin del juego.
            detenerJuego();
            System.out.println("¡LOS ZOMBIS SE HAN COMIDO TUS CEREBROS!");
        }
    }

    private void gestionarPlantas() {
        // Aquí programaremos más adelante la lógica para que los girasoles den soles
        // y los lanzaguisantes disparen a los zombis de su fila.
    }
}