package Backend.Guardado;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/** Lee exactamente la versión 1 emitida por GuardadorPartida. */
public class Cargar {
    private static final String FIRMA = "IPC1_PVZ_INVERSO";

    public static class PlantaGuardada {
        public String tipo;
        public int salud;
    }
    public static class ZombiGuardado {
        public String tipo;
        public int salud;
        public int fila;
        public double posicionX;
    }
    public static class GuisanteGuardado {
        public int fila;
        public double posicionX;
        public int dano;
        public int direccion;
        public boolean congelante;
    }
    public static class DatosPartida {
        public int duracionInicial;
        public int segundosRestantes;
        public int cerebrosActuales;
        public String dificultad;
        public int zombisInvocados;
        public int plantasDestruidas;
        public int bloque;
        public PlantaGuardada[][] plantas;
        public ZombiGuardado[] zombis;
        public GuisanteGuardado[] guisantes;
        public boolean[] podadorasActivas;
        public boolean[] podadorasEnMovimiento;
        public double[] posicionesPodadoras;
        public int[] cerebrosX;
        public int[] cerebrosY;

        public String resumen() {
            int numPlantas = 0;
            int numZombis = 0;
            for (int f = 0; f < plantas.length; f++) {
                for (int c = 0; c < plantas[f].length; c++) {
                    if (plantas[f][c] != null) numPlantas++;
                }
            }
            for (int i = 0; i < zombis.length; i++) {
                if (zombis[i] != null) numZombis++;
            }
            return "Archivo de partida leído correctamente."
                    + "\nDificultad: " + dificultad
                    + "\nTiempo restante: " + segundosRestantes + " segundos"
                    + "\nCerebros disponibles: " + cerebrosActuales
                    + "\nBloque de horda: " + bloque
                    + "\nPlantas: " + numPlantas
                    + "\nZombis: " + numZombis
                    + "\nCerebros visibles: " + cerebrosX.length
                    + "\n\nAVISO: la versión 1 no contiene estados de habilidades ni relojes."
                    + "\nEsta operación valida y lee el guardado; todavía no restaura el combate.";
        }
    }

    private int cantidad(DataInputStream in, int max, String etiqueta) throws IOException {
        int n = in.readInt();
        if (n < 0 || n > max) throw new IOException("Cantidad inválida en " + etiqueta + ": " + n);
        return n;
    }

    public DatosPartida leer(String ruta) throws IOException {
        File archivo = new File(ruta);
        if (!archivo.isFile()) throw new IOException("No existe: " + archivo.getPath());
        try (DataInputStream in = new DataInputStream(new BufferedInputStream(new FileInputStream(archivo)))) {
            if (!FIRMA.equals(in.readUTF())) throw new IOException("Firma de guardado incorrecta.");
            int version = in.readInt();
            if (version != 1) throw new IOException("Versión de guardado no compatible: " + version);
            DatosPartida d = new DatosPartida();
            d.duracionInicial = in.readInt();
            d.segundosRestantes = in.readInt();
            d.cerebrosActuales = in.readInt();
            d.dificultad = in.readUTF();
            d.zombisInvocados = in.readInt();
            d.plantasDestruidas = in.readInt();
            d.bloque = in.readInt();
            int filas = cantidad(in, 100, "filas");
            int columnas = cantidad(in, 100, "columnas");
            if (filas == 0 || columnas == 0 || d.segundosRestantes < 0 || d.cerebrosActuales < 0)
                throw new IOException("Dimensiones o valores inválidos en el guardado.");
            d.plantas = new PlantaGuardada[filas][columnas];
            for (int f = 0; f < filas; f++) {
                for (int c = 0; c < columnas; c++) {
                    if (in.readBoolean()) {
                        PlantaGuardada p = new PlantaGuardada();
                        p.tipo = in.readUTF();
                        p.salud = in.readInt();
                        d.plantas[f][c] = p;
                    }
                }
            }
            d.zombis = new ZombiGuardado[cantidad(in, 10000, "zombis")];
            for (int i = 0; i < d.zombis.length; i++) {
                if (in.readBoolean()) {
                    ZombiGuardado z = new ZombiGuardado();
                    z.tipo = in.readUTF();
                    z.salud = in.readInt();
                    z.fila = in.readInt();
                    z.posicionX = in.readDouble();
                    d.zombis[i] = z;
                }
            }
            d.guisantes = new GuisanteGuardado[cantidad(in, 50000, "guisantes")];
            for (int i = 0; i < d.guisantes.length; i++) {
                if (in.readBoolean()) {
                    GuisanteGuardado g = new GuisanteGuardado();
                    g.fila = in.readInt();
                    g.posicionX = in.readDouble();
                    g.dano = in.readInt();
                    g.direccion = in.readInt();
                    g.congelante = in.readBoolean();
                    d.guisantes[i] = g;
                }
            }
            int nPodadoras = cantidad(in, 100, "podadoras");
            d.podadorasActivas = new boolean[nPodadoras];
            d.podadorasEnMovimiento = new boolean[nPodadoras];
            d.posicionesPodadoras = new double[nPodadoras];
            for (int i = 0; i < nPodadoras; i++) {
                d.podadorasActivas[i] = in.readBoolean();
                d.podadorasEnMovimiento[i] = in.readBoolean();
                d.posicionesPodadoras[i] = in.readDouble();
            }
            int nCerebros = cantidad(in, 10000, "cerebros visibles");
            d.cerebrosX = new int[nCerebros];
            d.cerebrosY = new int[nCerebros];
            for (int i = 0; i < nCerebros; i++) {
                d.cerebrosX[i] = in.readInt();
                d.cerebrosY[i] = in.readInt();
            }
            if (in.read() != -1) throw new IOException("El archivo contiene información inesperada al final.");
            return d;
        }
    }
}
