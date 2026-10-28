package Backend.Estadisticas;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class ManejadorEstadisticas {
    private static final int FIRMA = 0x45535431; // EST1
    private static final int VERSION = 1;

    private File archivoDe(String nombre) throws IOException {
        if (nombre == null || !nombre.matches("[\\p{L}\\p{N} _-]{1,64}") || nombre.trim().isEmpty())
            throw new IOException("No hay una cuenta válida seleccionada");
        return new File(new File("cuentas", nombre.trim()), "estadisticas.dat");
    }

    public Estadisticas cargar(String nombre) throws IOException {
        File archivo = archivoDe(nombre);
        Estadisticas estadisticas = new Estadisticas(nombre.trim());
        if (!archivo.isFile()) return estadisticas;
        try (DataInputStream entrada = new DataInputStream(new BufferedInputStream(new FileInputStream(archivo)))) {
            if (entrada.readInt() != FIRMA || entrada.readInt() != VERSION)
                throw new IOException("Formato de estadísticas no reconocido: " + archivo);
            int jugadas = entrada.readInt(), ganadas = entrada.readInt(), perdidas = entrada.readInt();
            int plantas = entrada.readInt(), zombis = entrada.readInt(), cerebros = entrada.readInt();
            int tiempo = entrada.readInt(), podadoras = entrada.readInt();
            estadisticas.restaurar(jugadas, ganadas, perdidas, plantas, zombis, cerebros, tiempo, podadoras);
        } catch (IllegalArgumentException error) {
            throw new IOException("Archivo de estadísticas dañado: " + archivo, error);
        }
        return estadisticas;
    }

    public void registrar(String nombre, boolean victoria, int tiempo, int plantas,
                          int zombis, int cerebros, int podadoras) throws IOException {
        Estadisticas e = cargar(nombre);
        e.registrarResultado(victoria, tiempo, plantas, zombis, cerebros, podadoras);
        guardar(e);
    }

    private void guardar(Estadisticas e) throws IOException {
        File archivo = archivoDe(e.getJugador());
        File carpeta = archivo.getParentFile();
        if (!carpeta.isDirectory() && !carpeta.mkdirs())
            throw new IOException("No se pudo crear la carpeta: " + carpeta);
        File temporal = new File(carpeta, "estadisticas.tmp");
        try (DataOutputStream salida = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(temporal)))) {
            salida.writeInt(FIRMA); salida.writeInt(VERSION);
            salida.writeInt(e.getPartidasJugadas()); salida.writeInt(e.getPartidasGanadas());
            salida.writeInt(e.getPartidasPerdidas()); salida.writeInt(e.getPlantasDestruidas());
            salida.writeInt(e.getZombisInvocados()); salida.writeInt(e.getCerebrosGastados());
            salida.writeInt(e.getMejorTiempoSegundos()); salida.writeInt(e.getPodadorasUsadas());
        }
        try {
            Files.move(temporal.toPath(), archivo.toPath(), StandardCopyOption.REPLACE_EXISTING,
                    StandardCopyOption.ATOMIC_MOVE);
        } catch (java.nio.file.AtomicMoveNotSupportedException ex) {
            Files.move(temporal.toPath(), archivo.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
    }

    public Estadisticas[] obtenerTodas() {
        File raiz = new File("cuentas");
        File[] carpetas = raiz.listFiles();
        if (carpetas == null) return new Estadisticas[0];
        Estadisticas[] datos = new Estadisticas[carpetas.length];
        int cantidad = 0;
        for (int i = 0; i < carpetas.length; i++) {
            if (!carpetas[i].isDirectory() || !new File(carpetas[i], "estadisticas.dat").isFile()) continue;
            try {
                datos[cantidad++] = cargar(carpetas[i].getName());
            } catch (IOException ex) {
                System.err.println("No se pudo leer estadísticas de " + carpetas[i].getName() + ": " + ex.getMessage());
            }
        }
        Estadisticas[] resultado = new Estadisticas[cantidad];
        System.arraycopy(datos, 0, resultado, 0, cantidad);
        return resultado;
    }

    // tipo: 0 menor tiempo de victoria, 1 más plantas, 2 más victorias
    public Estadisticas[] topCinco(int tipo) {
        Estadisticas[] disponibles = obtenerTodas();
        Estadisticas[] top = new Estadisticas[5];
        boolean[] usados = new boolean[disponibles.length];
        for (int puesto = 0; puesto < 5; puesto++) {
            int mejor = -1;
            for (int i = 0; i < disponibles.length; i++) {
                if (usados[i] || (tipo == 0 && disponibles[i].getMejorTiempoSegundos() < 0)) continue;
                if (mejor < 0 || esMejor(disponibles[i], disponibles[mejor], tipo)) mejor = i;
            }
            if (mejor < 0) break;
            top[puesto] = disponibles[mejor];
            usados[mejor] = true;
        }
        return top;
    }

    private boolean esMejor(Estadisticas a, Estadisticas b, int tipo) {
        int x = tipo == 0 ? a.getMejorTiempoSegundos() : tipo == 1 ? a.getPlantasDestruidas() : a.getPartidasGanadas();
        int y = tipo == 0 ? b.getMejorTiempoSegundos() : tipo == 1 ? b.getPlantasDestruidas() : b.getPartidasGanadas();
        if (x != y) return tipo == 0 ? x < y : x > y;
        return a.getJugador().compareToIgnoreCase(b.getJugador()) < 0;
    }
}
