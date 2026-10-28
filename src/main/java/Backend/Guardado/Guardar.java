package Backend.Guardado;

import Backend.Modelos.Tablero;
import Backend.Modelos.Entidades.Plantas.Planta;
import Backend.Modelos.Entidades.Zombis.Zombi;
import Backend.Modelos.Entidades.Guisantes.Guisantes;
import java.io.DataOutputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/** Primera versión: escritura de una instantánea binaria, no carga. */
public class Guardar {
    private static final int VERSION = 1;

    public void guardar(String ruta, Tablero tablero, int duracionInicial,
                        int segundosRestantes, int cerebrosActuales, String dificultad,
                        int zombisInvocados, int plantasDestruidas, int bloque,
                        int[] cerebroX, int[] cerebroY) throws IOException {
        if (tablero == null) throw new IllegalArgumentException("Tablero nulo");
        if (cerebroX == null || cerebroY == null || cerebroX.length != cerebroY.length) {
            throw new IllegalArgumentException("Coordenadas de cerebros inconsistentes");
        }
        File destino = new File(ruta);
        File carpeta = destino.getParentFile();
        if (carpeta != null && !carpeta.exists() && !carpeta.mkdirs()) {
            throw new IOException("No se pudo crear: " + carpeta);
        }
        // Un formato versionado permite implementar la carga sin Java Serializable.
        try (DataOutputStream out = new DataOutputStream(
                new BufferedOutputStream(new FileOutputStream(destino)))) {
            out.writeUTF("IPC1_PVZ_INVERSO");
            out.writeInt(VERSION);
            out.writeInt(duracionInicial);
            out.writeInt(segundosRestantes);
            out.writeInt(cerebrosActuales);
            out.writeUTF(dificultad == null ? "Medio" : dificultad);
            out.writeInt(zombisInvocados);
            out.writeInt(plantasDestruidas);
            out.writeInt(bloque);
            out.writeInt(tablero.getFilas());
            out.writeInt(tablero.getColumnas());
            for (int fila = 0; fila < tablero.getFilas(); fila++) {
                for (int columna = 0; columna < tablero.getColumnas(); columna++) {
                    Planta p = tablero.getPlanta(fila, columna);
                    out.writeBoolean(p != null && p.estaViva());
                    if (p != null && p.estaViva()) {
                        out.writeUTF(p.getClass().getSimpleName());
                        out.writeInt(p.getSalud());
                    }
                }
            }
            out.writeInt(tablero.getListaZombis().getTamaño());
            for (int i = 0; i < tablero.getListaZombis().getTamaño(); i++) {
                Zombi z = tablero.getListaZombis().obtener(i);
                out.writeBoolean(z != null && z.estaVivo());
                if (z != null && z.estaVivo()) {
                    out.writeUTF(z.getClass().getSimpleName());
                    out.writeInt(z.getSalud());
                    out.writeInt(z.getFila());
                    out.writeDouble(z.getPosicionX());
                }
            }
            out.writeInt(tablero.getListaGuisantes().getTamaño());
            for (int i = 0; i < tablero.getListaGuisantes().getTamaño(); i++) {
                Guisantes g = tablero.getListaGuisantes().obtener(i);
                out.writeBoolean(g != null && g.estaActivo());
                if (g != null && g.estaActivo()) {
                    out.writeInt(g.getFila());
                    out.writeDouble(g.getPosicionX());
                    out.writeInt(g.getDaño());
                    out.writeInt(g.getDireccion());
                    out.writeBoolean(g.esCongelante());
                }
            }
            boolean[] activas = tablero.getPodadorasActivas();
            boolean[] moviendo = tablero.getPodadorasEnMovimiento();
            double[] posiciones = tablero.getPosicionesPodadoras();
            out.writeInt(activas.length);
            for (int i = 0; i < activas.length; i++) {
                out.writeBoolean(activas[i]);
                out.writeBoolean(moviendo[i]);
                out.writeDouble(posiciones[i]);
            }
            out.writeInt(cerebroX.length);
            for (int i = 0; i < cerebroX.length; i++) {
                out.writeInt(cerebroX[i]);
                out.writeInt(cerebroY[i]);
            }
        }
    }
}
