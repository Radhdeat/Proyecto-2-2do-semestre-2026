package Backend.Motor;

import Backend.Modelos.Tablero;
import Backend.Modelos.Entidades.Plantas.Planta;
import Backend.Modelos.Entidades.Plantas.Lanzaguisantes;
import Backend.Modelos.Entidades.Plantas.Hielaguisantes;
import Backend.Modelos.Entidades.Plantas.Bipetidora;
import Backend.Modelos.Entidades.Plantas.PinchoHierba;
import Backend.Modelos.Entidades.Guisantes.Guisantes;
import Backend.Modelos.Entidades.Zombis.Zombi;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;

import Backend.Modelos.Entidades.Plantas.Patatapum;
import Backend.Modelos.Entidades.Plantas.PlantaCarronivora;
import Backend.Modelos.Entidades.Zombis.Zombistein;
import Backend.Modelos.Entidades.Zombis.ZombiSaltador;
import Backend.Modelos.Entidades.Zombis.ZombiDeportista;
import Backend.Modelos.Entidades.Zombis.ZombiBailon;
import Backend.Modelos.Entidades.Zombis.Zombidito;
import Backend.Modelos.Entidades.Zombis.ZombiBailarin;
import Backend.Modelos.Entidades.Plantas.Nuez;

public class MotorJuego implements Runnable {

    private static final double ANCHO_JARDIN = 755.0;
    private static final double ANCHO_ZOMBI = 65.0;
    private static final double ANCHO_GUISANTE = 25.0;
    private static final double ANCHO_PODADORA = 55.0;
    private static final double VELOCIDAD_PODADORA = 8.0;
    private static final double VELOCIDAD_GUISANTE = 6.0;
    private static final int MILISEGUNDOS_POR_CICLO = 33;
    private static final int CICLOS_POR_ATAQUE = 30;
    private static final int CICLOS_DISPARO_NORMAL = 30;
    private static final int CICLOS_DISPARO_HIELO = 45;
    private static final long DURACION_CONGELACION_MS = 4000L;
    private static final double FACTOR_VELOCIDAD_CONGELADO = 0.60;

    private IA inteligenciaPlantas;
    private final Tablero tablero;
    private final Runnable actualizadorPantalla;
    private Runnable eventoVictoria;

    public interface EventoPlantaDestruida {
        void alDestruir(int fila, int columna);
    }

    private EventoPlantaDestruida eventoPlantaDestruida;
    private boolean juegoActivo;
    private Thread hiloMotor;
    private int contadorCiclos;
    private int plantasDestruidas;

    // Arreglos, sin ArrayList ni HashMap, para efectos de congelación.
    private Zombi[] zombisCongelados;
    private long[] congeladosHasta;
    // Tiempo del último ataque de cada pinchohierba (una vez cada 500 ms).
    private final long[][] ultimoAtaquePincho;
    private ZombiBailon[] bailonesEnBaile = new ZombiBailon[12];
    private long[] baileHasta = new long[12];

    private boolean juegoPausado = false;

    public MotorJuego(Tablero tablero, Runnable actualizadorPantalla) {
        this.tablero = tablero;
        this.actualizadorPantalla = actualizadorPantalla;
        this.juegoActivo = false;
        this.contadorCiclos = 0;
        this.plantasDestruidas = 0;
        this.zombisCongelados = new Zombi[20];
        this.congeladosHasta = new long[20];
        this.ultimoAtaquePincho = new long[tablero.getFilas()][tablero.getColumnas()];
    }

    public void setEventoPlantaDestruida(EventoPlantaDestruida evento) {
        this.eventoPlantaDestruida = evento;
    }

    public void setEventoVictoria(Runnable eventoVictoria) {
        this.eventoVictoria = eventoVictoria;
    }

    public void pausarJuego() {
        juegoPausado = true;
    }

    public void reanudarJuego() {
        juegoPausado = false;
    }

    public boolean isJuegoPausado() {
        return juegoPausado;
    }

    public void configurarInteligencia(String dificultad) {
        inteligenciaPlantas = new IA(tablero, dificultad);
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

                    if (juegoActivo && !juegoPausado) {

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
        if (inteligenciaPlantas != null) inteligenciaPlantas.actualizar();
        gestionarPodadoras();
        // La mina debe activarse antes de resolver los contactos.
        gestionarPatatapum();
        gestionarCarnivoras();
        moverYAtacarConZombis();
        if (!juegoActivo) return;
        gestionarPatatapum();
        gestionarCarnivoras();
        gestionarPinchoHierba();
        gestionarZombistein();
        gestionarBailones();

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
                // Si una carnívora está lista, tendrá prioridad en gestionarCarnivoras().
                if (intentarSaltar(zombi, obstaculo)) continue;
                atacarPlanta(zombi, obstaculo);
                continue;
            }

            if (estaBailando(zombi)) continue;
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
                if (intentarSaltar(zombi, nuevaColision)) continue;
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

    private boolean intentarSaltar(Zombi zombi, Planta planta) {
        if (!(zombi instanceof ZombiSaltador) || planta instanceof Nuez) return false;
        ZombiSaltador saltador = (ZombiSaltador) zombi;
        if (!saltador.tieneGarrocha()) return false;
        double anchoCasilla = ANCHO_JARDIN / tablero.getColumnas();
        double destino = (planta.getColumna() + 1) * anchoCasilla;
        saltador.setPosicionX(Math.min(destino, ANCHO_JARDIN - ANCHO_ZOMBI));
        saltador.perderGarrocha();
        return true;
    }

    private void registrarPlantaDestruida(int fila, int columna) {
        tablero.removerPlanta(fila, columna);
        plantasDestruidas++;
        if (eventoPlantaDestruida != null) {
            eventoPlantaDestruida.alDestruir(fila, columna);
        }
    }

    private void atacarPlanta(Zombi zombi, Planta planta) {
        // Zombistein pisa plantas en lugar de morderlas.
        if (zombi instanceof Zombistein) {
            registrarPlantaDestruida(planta.getFila(), planta.getColumna());
            return;
        }
        int intervalo = zombi instanceof ZombiDeportista ? 15 : CICLOS_POR_ATAQUE;
        if (contadorCiclos % intervalo != 0) return;
        planta.recibirDaño(zombi.getDaño());
        if (!planta.estaViva()) {
            registrarPlantaDestruida(planta.getFila(), planta.getColumna());
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

                // Alcanza su propia casilla y la frontal (hacia la izquierda).
                double inicioCasilla = Math.max(0, (columna - 1) * anchoCasilla);
                double finalCasilla = (columna + 1) * anchoCasilla;

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

    private void gestionarPinchoHierba() {
        if (tablero.getColumnas() <= 0) {
            return;
        }
        long ahora = System.currentTimeMillis();
        double anchoCasilla = ANCHO_JARDIN / tablero.getColumnas();

        for (int fila = 0; fila < tablero.getFilas(); fila++) {
            for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                Planta planta = tablero.getPlanta(fila, columna);
                if (!(planta instanceof PinchoHierba) || !planta.estaViva()) {
                    ultimoAtaquePincho[fila][columna] = 0;
                    continue;
                }

                double izquierda = columna * anchoCasilla;
                double derecha = izquierda + anchoCasilla;
                boolean hayZombis = false;
                boolean aplastada = false;

                for (int i = 0; i < tablero.getListaZombis().getTamaño(); i++) {
                    Zombi zombi = tablero.getListaZombis().obtener(i);
                    if (zombi == null || !zombi.estaVivo() || zombi.getFila() != fila) {
                        continue;
                    }
                    double inicioZombi = zombi.getPosicionX();
                    double finZombi = inicioZombi + ANCHO_ZOMBI;
                    if (inicioZombi >= derecha || finZombi <= izquierda) {
                        continue;
                    }
                    // La destrucción ocurre al pasar por encima, no por mordidas.
                    if (zombi instanceof Zombistein) {
                        aplastada = true;
                        break;
                    }
                    hayZombis = true;
                }

                if (aplastada) {
                    registrarPlantaDestruida(fila, columna);
                    ultimoAtaquePincho[fila][columna] = 0;
                    continue;
                }
                if (!hayZombis) {
                    ultimoAtaquePincho[fila][columna] = 0;
                    continue;
                }
                if (ultimoAtaquePincho[fila][columna] != 0
                        && ahora - ultimoAtaquePincho[fila][columna] < 500L) {
                    continue;
                }
                ultimoAtaquePincho[fila][columna] = ahora;
                for (int i = 0; i < tablero.getListaZombis().getTamaño(); i++) {
                    Zombi zombi = tablero.getListaZombis().obtener(i);
                    if (zombi == null || !zombi.estaVivo() || zombi.getFila() != fila) {
                        continue;
                    }
                    double x = zombi.getPosicionX();
                    if (x < derecha && x + ANCHO_ZOMBI > izquierda) {
                        zombi.recibirDaño(10);
                    }
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
            if (planta == null
                    || !planta.estaViva()
                    || planta instanceof PinchoHierba) {

                continue;
            }
            // Inactiva y Activa:
            if (planta instanceof Patatapum && ((Patatapum) planta).isActiva()) {
                continue;
            }
            double izquierdaPlanta = columna * anchoCasilla;
            double derechaPlanta = izquierdaPlanta + anchoCasilla;
            if (derechaZombi >= izquierdaPlanta && izquierdaZombi <= derechaPlanta) {
                return planta;
            }
        }
        return null;
    }

    private void gestionarZombistein() {
        // Iteramos solamente zombis preexistentes; no repetimos el lanzamiento.
        int cantidadInicial = tablero.getListaZombis().getTamaño();
        for (int i = 0; i < cantidadInicial; i++) {
            Zombi zombi = tablero.getListaZombis().obtener(i);
            if (!(zombi instanceof Zombistein) || !zombi.estaVivo()) continue;
            Zombistein gigante = (Zombistein) zombi;
            if (!gigante.puedeLanzarZombidito()) continue;
            // No lanzar si ya se encuentra en las últimas dos casillas.
            double limite = (tablero.getColumnas() - 2)
                    * ANCHO_JARDIN / tablero.getColumnas();
            if (gigante.getPosicionX() >= limite) continue;
            double destino = Math.min(gigante.getPosicionX() + ANCHO_ZOMBI,
                    ANCHO_JARDIN - ANCHO_ZOMBI);
            gigante.registrarLanzamiento();
            tablero.agregarZombi(new Zombidito(gigante.getFila(), destino));
        }
    }

    private void gestionarBailones() {
        int cantidadInicial = tablero.getListaZombis().getTamaño();
        for (int i = 0; i < cantidadInicial; i++) {
            Zombi zombi = tablero.getListaZombis().obtener(i);
            if (!(zombi instanceof ZombiBailon) || !zombi.estaVivo()) continue;
            ZombiBailon bailon = (ZombiBailon) zombi;
            if (!bailon.puedeBailar()) continue;
            bailon.registrarBaile();
            registrarBaileActivo(bailon);
            int fila = bailon.getFila();
            double x = bailon.getPosicionX();
            double anchoCelda = ANCHO_JARDIN / tablero.getColumnas();
            // Vecinos: arriba, abajo, delante y detrás. No solapar zombis.
            colocarBailarin(fila - 1, x);
            colocarBailarin(fila + 1, x);
            colocarBailarin(fila, x - anchoCelda);
            colocarBailarin(fila, x + anchoCelda);
        }
    }

    private void registrarBaileActivo(ZombiBailon bailon) {
        long hasta = System.currentTimeMillis() + 1000L;
        int libre = -1;
        for (int i = 0; i < bailonesEnBaile.length; i++) {
            if (bailonesEnBaile[i] == bailon) {
                baileHasta[i] = hasta;
                return;
            }
            if (libre == -1 && (bailonesEnBaile[i] == null
                    || baileHasta[i] <= System.currentTimeMillis())) libre = i;
        }
        if (libre == -1) {
            int anterior = bailonesEnBaile.length;
            ZombiBailon[] nuevos = new ZombiBailon[anterior * 2];
            long[] nuevosHasta = new long[anterior * 2];
            System.arraycopy(bailonesEnBaile, 0, nuevos, 0, anterior);
            System.arraycopy(baileHasta, 0, nuevosHasta, 0, anterior);
            bailonesEnBaile = nuevos;
            baileHasta = nuevosHasta;
            libre = anterior;
        }
        bailonesEnBaile[libre] = bailon;
        baileHasta[libre] = hasta;
    }

    private boolean estaBailando(Zombi zombi) {
        if (!(zombi instanceof ZombiBailon)) return false;
        for (int i = 0; i < bailonesEnBaile.length; i++) {
            if (bailonesEnBaile[i] == zombi) {
                if (System.currentTimeMillis() < baileHasta[i]) return true;
                bailonesEnBaile[i] = null;
                return false;
            }
        }
        return false;
    }

    private void colocarBailarin(int fila, double posicionX) {
        if (fila < 0 || fila >= tablero.getFilas()) return;
        if (posicionX < 0 || posicionX + ANCHO_ZOMBI > ANCHO_JARDIN) return;
        for (int i = 0; i < tablero.getListaZombis().getTamaño(); i++) {
            Zombi existente = tablero.getListaZombis().obtener(i);
            if (existente != null && existente.estaVivo()
                    && existente.getFila() == fila
                    && Math.abs(existente.getPosicionX() - posicionX) < ANCHO_ZOMBI) {
                return;
            }
        }
        tablero.agregarZombi(new ZombiBailarin(fila, posicionX));
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

