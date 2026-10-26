package Backend.Motor;

import Backend.Modelo.Tablero;
import Backend.Modelo.Entidades.Plantas.Planta;
import Backend.Modelo.Entidades.Plantas.Lanzaguisantes;
import Backend.Modelo.Entidades.Plantas.Hielaguisantes;
import Backend.Modelo.Entidades.Plantas.Bipetidora;
import Backend.Modelo.Entidades.Plantas.PinchoHierba;
import Backend.Modelo.Entidades.Guisantes.Guisantes;
import Backend.Modelo.Entidades.Zombis.Zombi;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

import Backend.Modelo.Entidades.Plantas.Patatapum;
import Backend.Modelo.Entidades.Plantas.PlantaCarronivora;
import Backend.Modelo.Entidades.Zombis.Zombistein;

public class MotorJuego implements Runnable {

    private static final double ANCHO_JARDIN = 755.0;
    private static final double ANCHO_ZOMBI = 50.0;
    private static final double ANCHO_GUISANTE = 25.0;
    private static final double ANCHO_PODADORA = 55.0;
    private static final double VELOCIDAD_PODADORA = 8.0;
    private static final double VELOCIDAD_GUISANTE = 6.0;
    private static final int MILISEGUNDOS_POR_CICLO = 33;
    private static final int CICLOS_POR_ATAQUE = 30;
    private static final int CICLOS_DISPARO_NORMAL = 30;
    private static final int CICLOS_DISPARO_HIELO = 45;
    private static final long DURACION_CONGELACION_MS = 3000L;
    private static final double FACTOR_VELOCIDAD_CONGELADO = 0.60;

    private final Tablero tablero;
    private final Runnable actualizadorPantalla;
    private Runnable eventoVictoria;
    private boolean juegoActivo;
    private Thread hiloMotor;
    private int contadorCiclos;
    private int plantasDestruidas;

    // Arreglos, sin ArrayList ni HashMap, para efectos de congelación.
    private Zombi[] zombisCongelados;
    private long[] congeladosHasta;

    public MotorJuego(Tablero tablero, Runnable actualizadorPantalla) {
        this.tablero = tablero;
        this.actualizadorPantalla = actualizadorPantalla;
        this.juegoActivo = false;
        this.contadorCiclos = 0;
        this.plantasDestruidas = 0;
        this.zombisCongelados = new Zombi[20];
        this.congeladosHasta = new long[20];
    }

    public void setEventoVictoria(Runnable eventoVictoria) {
        this.eventoVictoria = eventoVictoria;
    }

    public void iniciarJuego() {
        if (juegoActivo) {
            return;
        }
        juegoActivo = true;
        hiloMotor = new Thread(this, "MotorJuego");
        hiloMotor.start();
    }

    public void detenerJuego() {
        juegoActivo = false;
        if (hiloMotor != null && hiloMotor != Thread.currentThread()) {
            hiloMotor.interrupt();
        }
    }

    @Override
    public void run() {
        // Toda modificación de las listas ocurre en el EDT, al igual que el dibujo.
        while (!Thread.currentThread().isInterrupted()) {
            try {
                SwingUtilities.invokeAndWait(() -> {
                    if (juegoActivo) {
                        actualizarLogica();
                        if (actualizadorPantalla != null) {
                            actualizadorPantalla.run();
                        }
                    }
                });
                if (!juegoActivo) {
                    break;
                }
                Thread.sleep(MILISEGUNDOS_POR_CICLO);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            } catch (InvocationTargetException e) {
                System.err.println("Error en el motor: " + e.getCause());
                detenerJuego();
                break;
            }
        }
    }

    private void actualizarLogica() {
        contadorCiclos++;
        gestionarPodadoras();
        moverYAtacarConZombis();
        gestionarPatatapum();
        gestionarCarnivoras();

        if (!juegoActivo) {
            return;
        }

        // Las plantas disparan de acuerdo con su cadencia.
        if (contadorCiclos % CICLOS_DISPARO_NORMAL == 0) {
            dispararPlantasNormales();
        }
        if (contadorCiclos % CICLOS_DISPARO_HIELO == 0) {
            dispararHielaguisantes();
        }

        moverGuisantesYDetectarImpactos();
        tablero.limpiarZombisMuertos();
        tablero.limpiarGuisantesInactivos();
    }

    private void moverYAtacarConZombis() {
        for (int i = 0; i < tablero.getListaZombis().getTamaño(); i++) {
            Zombi zombi = tablero.getListaZombis().obtener(i);
            if (zombi == null || !zombi.estaVivo()) {
                continue;
            }
            int fila = zombi.getFila();
            if (fila < 0 || fila >= tablero.getFilas()) {
                continue;
            }

            Planta obstaculo = encontrarPlantaEnContacto(zombi);
            if (obstaculo != null) {
                atacarPlanta(zombi, obstaculo);
                continue;
            }

            double posicionAnterior = zombi.getPosicionX();
            zombi.avanzar();
            if (estaCongelado(zombi)) {
                double desplazamiento = zombi.getPosicionX() - posicionAnterior;
                zombi.setPosicionX(posicionAnterior
                        + desplazamiento * FACTOR_VELOCIDAD_CONGELADO);
            }

            Planta nuevaColision = encontrarPlantaEnContacto(zombi);
            if (nuevaColision != null) {
                // El zombi se detiene justo antes de la planta bloqueante.
                double anchoCasilla = ANCHO_JARDIN / tablero.getColumnas();
                double limite = nuevaColision.getColumna() * anchoCasilla - ANCHO_ZOMBI;
                zombi.setPosicionX(Math.min(zombi.getPosicionX(), limite));
                atacarPlanta(zombi, nuevaColision);
                continue;
            }

            if (zombi.getPosicionX() + ANCHO_ZOMBI >= ANCHO_JARDIN) {
                evaluarInvasion(zombi);
                if (!juegoActivo) {
                    return;
                }
            }
        }
    }

    private void atacarPlanta(Zombi zombi, Planta planta) {
        if (contadorCiclos % CICLOS_POR_ATAQUE != 0) {
            return;
        }
        planta.recibirDaño(zombi.getDaño());
        if (!planta.estaViva()) {
            tablero.removerPlanta(planta.getFila(), planta.getColumna());
            plantasDestruidas++;
        }
    }
    private void gestionarPatatapum() {

        double anchoCasilla =
                ANCHO_JARDIN / tablero.getColumnas();

        for (int fila = 0; fila < tablero.getFilas(); fila++) {

            for (int columna = 0;
                 columna < tablero.getColumnas();
                 columna++) {

                Planta planta = tablero.getPlanta(fila, columna);

                if (!(planta instanceof Patatapum)) {
                    continue;
                }

                Patatapum patatapum = (Patatapum) planta;

                if (!patatapum.estaViva()) {
                    continue;
                }

                patatapum.actuar();

                if (!patatapum.isActiva()) {
                    continue;
                }

                double inicioCasilla = columna * anchoCasilla;
                double finalCasilla = inicioCasilla + anchoCasilla;

                boolean explotar = false;

                for (int i = 0;
                     i < tablero.getListaZombis().getTamaño();
                     i++) {

                    Zombi zombi =
                            tablero.getListaZombis().obtener(i);

                    if (zombi == null || !zombi.estaVivo()
                            || zombi.getFila() != fila) {
                        continue;
                    }

                    double inicioZombi = zombi.getPosicionX();
                    double finalZombi = inicioZombi + ANCHO_ZOMBI;

                    if (finalZombi >= inicioCasilla
                            && inicioZombi <= finalCasilla) {

                        explotar = true;
                        break;
                    }
                }

                if (!explotar) {
                    continue;
                }

                System.out.println(
                        "¡PATATAPUM EXPLOTÓ! Fila: " + fila
                                + ", columna: " + columna
                );

                for (int i = 0;
                     i < tablero.getListaZombis().getTamaño();
                     i++) {

                    Zombi zombi =
                            tablero.getListaZombis().obtener(i);

                    if (zombi == null || !zombi.estaVivo()
                            || zombi.getFila() != fila) {
                        continue;
                    }

                    double inicioZombi = zombi.getPosicionX();
                    double finalZombi = inicioZombi + ANCHO_ZOMBI;

                    if (finalZombi >= inicioCasilla
                            && inicioZombi <= finalCasilla) {

                        zombi.recibirDaño(
                                patatapum.getDanioExplosion()
                        );

                        System.out.println(
                                "Daño de Patatapum: "
                                        + patatapum.getDanioExplosion()
                                        + " | Salud restante: "
                                        + zombi.getSalud()
                        );
                    }
                }

                tablero.removerPlanta(fila, columna);
            }
        }
    }

    private void gestionarCarnivoras() {

        double anchoCasilla =
                ANCHO_JARDIN / tablero.getColumnas();

        for (int fila = 0; fila < tablero.getFilas(); fila++) {

            for (int columna = 0;
                 columna < tablero.getColumnas();
                 columna++) {

                Planta planta = tablero.getPlanta(fila, columna);

                if (!(planta instanceof PlantaCarronivora)) {
                    continue;
                }

                PlantaCarronivora carnivora =
                        (PlantaCarronivora) planta;

                if (!carnivora.estaViva()) {
                    continue;
                }

                // Comprobar si terminó de digerir.
                carnivora.actuar();

                if (!carnivora.puedeAtacar()) {
                    continue;
                }

                double inicioCasilla = columna * anchoCasilla;
                double finalCasilla = inicioCasilla + anchoCasilla;

                for (int i = 0;
                     i < tablero.getListaZombis().getTamaño();
                     i++) {

                    Zombi zombi =
                            tablero.getListaZombis().obtener(i);

                    if (zombi == null || !zombi.estaVivo()) {
                        continue;
                    }

                    // Solo puede atacar zombis de su fila.
                    if (zombi.getFila() != fila) {
                        continue;
                    }

                    double inicioZombi = zombi.getPosicionX();

                    double finalZombi =
                            inicioZombi + ANCHO_ZOMBI;

                    // Detectar contacto con la planta.
                    boolean contacto =
                            finalZombi >= inicioCasilla
                                    && inicioZombi <= finalCasilla;

                    if (!contacto) {
                        continue;
                    }

                    // Comportamiento especial contra Zombistein.
                    if (zombi instanceof Zombistein) {

                        zombi.recibirDaño(
                                carnivora.getDañoZombistein()
                        );

                        System.out.println(
                                "Planta carnívora atacó a Zombistein."
                        );

                        System.out.println(
                                "Salud restante: " + zombi.getSalud()
                        );

                    } else {

                        // Devorar completamente al zombi.
                        zombi.recibirDaño(zombi.getSalud());

                        System.out.println(
                                "¡La planta carnívora devoró un zombi!"
                        );
                    }

                    // Comenzar los 8 segundos de digestión.
                    carnivora.comenzarDigestion();

                    // Solo puede atacar a un zombi por vez.
                    break;
                }
            }
        }
    }

    private Planta encontrarPlantaEnContacto(Zombi zombi) {

        int fila = zombi.getFila();
        if (fila < 0 || fila >= tablero.getFilas() || tablero.getColumnas() == 0) {
            return null;
        }

        double anchoCasilla = ANCHO_JARDIN / tablero.getColumnas();
        double izquierdaZombi = zombi.getPosicionX();
        double derechaZombi = izquierdaZombi + ANCHO_ZOMBI;

        for (int columna = 0; columna < tablero.getColumnas(); columna++) {
            Planta planta = tablero.getPlanta(fila, columna);
            if (planta == null || !planta.estaViva() || planta instanceof PinchoHierba) {
                continue;
            }
            double izquierdaPlanta = columna * anchoCasilla;
            double derechaPlanta = izquierdaPlanta + anchoCasilla;
            if (derechaZombi > izquierdaPlanta && izquierdaZombi < derechaPlanta) {
                return planta;
            }
        }
        return null;
    }

    private void dispararPlantasNormales() {
        double anchoCasilla = ANCHO_JARDIN / tablero.getColumnas();
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                Planta planta = tablero.getPlanta(fila, columna);
                if (planta == null || !planta.estaViva()) {
                    continue;
                }
                double izquierda = columna * anchoCasilla;
                double derecha = izquierda + anchoCasilla;

                if (planta instanceof Lanzaguisantes) {
                    if (hayZombiEnDireccion(fila, izquierda, -1)) {
                        Lanzaguisantes lanzador = (Lanzaguisantes) planta;
                        crearGuisante(izquierda - ANCHO_GUISANTE, fila,
                                lanzador.getDañoProyectil(), -1, false);
                    }
                } else if (planta instanceof Bipetidora) {
                    Bipetidora bipetidora = (Bipetidora) planta;
                    if (hayZombiEnDireccion(fila, izquierda, -1)) {
                        crearGuisante(izquierda - ANCHO_GUISANTE, fila,
                                bipetidora.getDañoProyectil(), -1, false);
                    }
                    if (hayZombiEnDireccion(fila, derecha, 1)) {
                        crearGuisante(derecha, fila,
                                bipetidora.getDañoProyectil(), 1, false);
                    }
                }
            }
        }
    }

    private void dispararHielaguisantes() {
        double anchoCasilla = ANCHO_JARDIN / tablero.getColumnas();
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                Planta planta = tablero.getPlanta(fila, columna);
                if (!(planta instanceof Hielaguisantes) || !planta.estaViva()) {
                    continue;
                }
                double izquierda = columna * anchoCasilla;
                if (hayZombiEnDireccion(fila, izquierda, -1)) {
                    Hielaguisantes hielaguisantes = (Hielaguisantes) planta;
                    crearGuisante(izquierda - ANCHO_GUISANTE, fila,
                            hielaguisantes.getDañoProyectil(), -1, true);
                }
            }
        }
    }

    private void crearGuisante(double posicionX, int fila, int danio,
                               int direccion, boolean congelante) {
        Guisantes guisante = new Guisantes(posicionX, fila, danio,
                VELOCIDAD_GUISANTE, direccion, congelante);
        tablero.agregarGuisante(guisante);
    }

    private boolean hayZombiEnDireccion(int fila, double posicionPlanta,
                                        int direccion) {
        for (int i = 0; i < tablero.getListaZombis().getTamaño(); i++) {
            Zombi zombi = tablero.getListaZombis().obtener(i);
            if (zombi == null || !zombi.estaVivo() || zombi.getFila() != fila) {
                continue;
            }
            if (direccion < 0 && zombi.getPosicionX() < posicionPlanta) {
                return true;
            }
            if (direccion > 0 && zombi.getPosicionX() + ANCHO_ZOMBI > posicionPlanta) {
                return true;
            }
        }
        return false;
    }

    private void moverGuisantesYDetectarImpactos() {
        for (int i = 0; i < tablero.getListaGuisantes().getTamaño(); i++) {
            Guisantes guisante = tablero.getListaGuisantes().obtener(i);
            if (guisante == null || !guisante.estaActivo()) {
                continue;
            }
            double anterior = guisante.getPosicionX();
            guisante.avanzar();
            double actual = guisante.getPosicionX();
            double inicioBarrido = Math.min(anterior, actual);
            double finBarrido = Math.max(anterior, actual) + ANCHO_GUISANTE;

            Zombi objetivo = null;
            double menorDistancia = Double.MAX_VALUE;
            for (int j = 0; j < tablero.getListaZombis().getTamaño(); j++) {
                Zombi zombi = tablero.getListaZombis().obtener(j);
                if (zombi == null || !zombi.estaVivo()
                        || zombi.getFila() != guisante.getFila()) {
                    continue;
                }
                double inicioZombi = zombi.getPosicionX();
                double finZombi = inicioZombi + ANCHO_ZOMBI;
                if (inicioBarrido < finZombi && finBarrido > inicioZombi) {
                    double distancia = guisante.getDireccion() < 0
                            ? Math.abs(anterior - finZombi)
                            : Math.abs(inicioZombi - (anterior + ANCHO_GUISANTE));
                    if (distancia < menorDistancia) {
                        menorDistancia = distancia;
                        objetivo = zombi;
                    }
                }
            }
            if (objetivo != null) {
                objetivo.recibirDaño(guisante.getDaño());
                if (guisante.esCongelante() && objetivo.estaVivo()) {
                    aplicarCongelacion(objetivo);
                }
                guisante.desactivar();
            }
            if (actual + ANCHO_GUISANTE < 0 || actual > ANCHO_JARDIN) {
                guisante.desactivar();
            }
        }
    }

    private void aplicarCongelacion(Zombi zombi) {
        long vencimiento = System.currentTimeMillis() + DURACION_CONGELACION_MS;
        int libre = -1;
        for (int i = 0; i < zombisCongelados.length; i++) {
            if (zombisCongelados[i] == zombi) {
                congeladosHasta[i] = vencimiento;
                return;
            }
            if (libre == -1 && (zombisCongelados[i] == null
                    || !zombisCongelados[i].estaVivo()
                    || congeladosHasta[i] <= System.currentTimeMillis())) {
                libre = i;
            }
        }
        if (libre == -1) {
            int anterior = zombisCongelados.length;
            Zombi[] nuevosZombis = new Zombi[anterior * 2];
            long[] nuevosVencimientos = new long[anterior * 2];
            System.arraycopy(zombisCongelados, 0, nuevosZombis, 0, anterior);
            System.arraycopy(congeladosHasta, 0, nuevosVencimientos, 0, anterior);
            zombisCongelados = nuevosZombis;
            congeladosHasta = nuevosVencimientos;
            libre = anterior;
        }
        zombisCongelados[libre] = zombi;
        congeladosHasta[libre] = vencimiento;
    }

    private boolean estaCongelado(Zombi zombi) {
        long ahora = System.currentTimeMillis();
        for (int i = 0; i < zombisCongelados.length; i++) {
            if (zombisCongelados[i] == zombi) {
                if (congeladosHasta[i] > ahora) {
                    return true;
                }
                zombisCongelados[i] = null;
                return false;
            }
        }
        return false;
    }

    private void evaluarInvasion(Zombi zombi) {
        int fila = zombi.getFila();
        if (tablero.getPodadorasActivas()[fila]) {
            tablero.activarPodadora(fila);
            zombi.recibirDaño(zombi.getSalud());
            return;
        }
        if (tablero.getPodadorasEnMovimiento()[fila]) {
            // No permitir cruzar la casa mientras se mueve la podadora.
            zombi.recibirDaño(zombi.getSalud());
            return;
        }
        juegoActivo = false;
        if (eventoVictoria != null) {
            eventoVictoria.run();
        }
    }

    private void gestionarPodadoras() {
        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            if (!tablero.getPodadorasEnMovimiento()[fila]) {
                continue;
            }
            double anterior = tablero.getPosicionesPodadoras()[fila];
            tablero.moverPodadora(fila, VELOCIDAD_PODADORA);
            double actual = tablero.getPosicionesPodadoras()[fila];

            for (int i = 0; i < tablero.getListaZombis().getTamaño(); i++) {
                Zombi zombi = tablero.getListaZombis().obtener(i);
                if (zombi == null || !zombi.estaVivo() || zombi.getFila() != fila) {
                    continue;
                }
                if (zombi.getPosicionX() < anterior + ANCHO_PODADORA
                        && zombi.getPosicionX() + ANCHO_ZOMBI > actual) {
                    zombi.recibirDaño(zombi.getSalud());
                }
            }
        }
    }

    public int getPlantasDestruidas() {
        return plantasDestruidas;
    }

    public boolean isJuegoActivo() {
        return juegoActivo;
    }
}
