package Backend.Motor;

import Backend.Modelos.Nivel;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class LectorNivel {

    public static Nivel cargarNivelDesdeJSON(String rutaArchivo) {
        Nivel nivel = new Nivel();
        StringBuilder contenidoJSON = new StringBuilder();

        // 1. Leer todo el archivo JSON usando java.io
        try (BufferedReader reader = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                contenidoJSON.append(linea.trim());
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de nivel: " + e.getMessage());
            return null;
        }

        String json = contenidoJSON.toString();

        // 2. Extraer valores clave mediante manipulación de cadenas (String)
        nivel.setIdNivel(extraerEntero(json, "nivel"));
        nivel.setSolesIniciales(extraerEntero(json, "solesIniciales"));
        nivel.setCantidadOleadas(extraerEntero(json, "cantidadOleadas"));

        // 3. Extraer arreglos del JSON
        extraerArreglo(json, "zombis", nivel.getTiposZombisPermitidos());
        extraerArreglo(json, "plantas", nivel.getTiposPlantasDisponibles());

        return nivel;
    }

    // Método auxiliar para buscar "clave": valor_entero
    private static int extraerEntero(String json, String clave) {
        String patron = "\"" + clave + "\"";
        int inicioClave = json.indexOf(patron);
        if (inicioClave == -1) return 0;

        int inicioValor = json.indexOf(":", inicioClave) + 1;
        int finValor = json.indexOf(",", inicioValor);
        if (finValor == -1) {
            finValor = json.indexOf("}", inicioValor);
        }

        String valorTexto = json.substring(inicioValor, finValor).replaceAll("[^0-9]", "").trim();
        return valorTexto.isEmpty() ? 0 : Integer.parseInt(valorTexto);
    }

    // Método auxiliar para extraer elementos de un arreglo "clave": ["item1", "item2"]
    private static void extraerArreglo(String json, String clave, Backend.Extructuras.ListaDinamica<String> listaDestino) {
        String patron = "\"" + clave + "\"";
        int inicioClave = json.indexOf(patron);
        if (inicioClave == -1) return;

        int inicioCorchete = json.indexOf("[", inicioClave);
        int finCorchete = json.indexOf("]", inicioCorchete);

        if (inicioCorchete != -1 && finCorchete != -1) {
            String contenidoArreglo = json.substring(inicioCorchete + 1, finCorchete);
            String[] elementos = contenidoArreglo.split(",");

            for (String elem : elementos) {
                String limpio = elem.replace("\"", "").trim();
                if (!limpio.isEmpty()) {
                    listaDestino.agregar(limpio);
                }
            }
        }
    }
}