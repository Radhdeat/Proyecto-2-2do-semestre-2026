package Backend.Cuentas;

// importa lo necesario para trabajar archivos
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

//clase para leer los archivos Json
public class LectorNivelJson {

    // declaramos atributos
    private int duracionSegundos;
    private int cerebrosIniciales;
    private String[][] jardin;

    // metodo que recibe la ruta del archivo
    public boolean cargarConfiguracion(String rutaArchivo) {
        // se crea un objeto vacio
        StringBuilder sb = new StringBuilder();
        // se lee el archivo completo
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            // se hace referencia a la linea actual
            String linea;
            // repite la lectura mientras hayan lineas
            while ((linea = br.readLine()) != null) {
                // agrega la linea leida al bufferReader
                sb.append(linea.trim());
            }
            // por en caso de que haya un error en la lectura
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON: " + e.getMessage());
            return false;
        }

        // al objeto vacio lo volvemos una cadena de texto y lo guarda
        String contenido = sb.toString();

        // Extraer propiedades principales
        this.duracionSegundos = extraerEntero(contenido, "duracionSegundos");
        this.cerebrosIniciales = extraerEntero(contenido, "cerebrosIniciales");

        //Extraer y validar el arreglo del jardín
        this.jardin = extraerJardinNativo(contenido);
        if (this.jardin == null) {
            return false;
        }

        return true;
    }

    // extrae por completo el jardin
    private int extraerEntero(String json, String clave) {
        // se usan las \ para la busqueda de las propiedades del objeto
        String busqueda = "\"" + clave + "\"";
        // busca donde comienza el objeto
        int posicionClave = json.indexOf(busqueda);
        if (posicionClave == -1) return -1;

        // busca : para leer despues de esa posicion
        int posDosPuntos = json.indexOf(":", posicionClave);
        if (posDosPuntos == -1) return -1;

        // crea un objeto vacaio
        StringBuilder valor = new StringBuilder();
        // indica si ya empezo a leer los archivos
        boolean leyendo = false;
        // recorre el archivo hasta que se acabe
        for (int i = posDosPuntos + 1; i < json.length(); i++) {
            // almacena la posicion i
            char c = json.charAt(i);
            // si es un digito numerocio
            if (Character.isDigit(c)) {
                // empieza a leer
                leyendo = true;
                valor.append(c);
            } else if (leyendo) {
                break;
            }
        }

        // si no encontro un valor devuelve -1
        if (valor.length() == 0) return -1;
        // convierte el texto en entero para devolverlo
        return Integer.parseInt(valor.toString());
    }

    // Método robusto: encuentra el cierre exacto del arreglo contando corchetes
    private String[][] extraerJardinNativo(String json) {

        int posJardin = json.indexOf("\"jardin\"");
        if (posJardin == -1) return null;

        int inicioCorchete = json.indexOf("[", posJardin);
        if (inicioCorchete == -1) return null;

        // Contar la profundidad de corchetes para hallar su cierre exacto
        int corchetesAbiertos = 0;
        int finCorchete = -1;
        for (int i = inicioCorchete; i < json.length(); i++) {
            char c = json.charAt(i);
            if (c == '[') {
                corchetesAbiertos++;
            } else if (c == ']') {
                corchetesAbiertos--;
                if (corchetesAbiertos == 0) {
                    finCorchete = i;
                    break;
                }
            }
        }

        if (finCorchete == -1) return null;

        String contenidoJardin = json.substring(inicioCorchete + 1, finCorchete);

        // Contar filas (carriles) contando los corchetes internos '['
        int totalFilas = 0;
        for (int i = 0; i < contenidoJardin.length(); i++) {
            if (contenidoJardin.charAt(i) == '[') totalFilas++;
        }

        if (totalFilas == 0) return null;

        String[][] temporalCarriles = new String[totalFilas][];
        int filaActual = 0;

        int i = 0;
        while (i < contenidoJardin.length()) {
            int inicioCarril = contenidoJardin.indexOf("[", i);
            if (inicioCarril == -1) break;
            int finCarril = contenidoJardin.indexOf("]", inicioCarril);
            if (finCarril == -1) break;

            String strCarril = contenidoJardin.substring(inicioCarril + 1, finCarril);
            String[] celdas = strCarril.split(",");

            temporalCarriles[filaActual] = new String[celdas.length];
            for (int j = 0; j < celdas.length; j++) {
                temporalCarriles[filaActual][j] = celdas[j].trim().replace("\"", "");
            }

            filaActual++;
            i = finCarril + 1;
        }

        // Validar que todas las columnas sean iguales
        int columnasEsperadas = temporalCarriles[0].length;
        for (String[] carril : temporalCarriles) {
            if (carril.length != columnasEsperadas) {
                System.err.println("Error: Los carriles tienen distinta cantidad de columnas[cite: 1].");
                return null;
            }
        }

        return temporalCarriles;
    }

    private boolean esIdentificadorValido(String id) {
        return id.equals("VACIO") ||
                id.equals("LANZAGUISANTES") ||
                id.equals("PATATAPUM") ||
                id.equals("HIELAGUISANTES") ||
                id.equals("NUEZ") ||
                id.equals("CARRONIVORA") ||
                id.equals("PINCHOHIERBA") ||
                id.equals("BIPETIDORA");
    }

    public int getDuracionSegundos() { return duracionSegundos; }
    public int getCerebrosIniciales() { return cerebrosIniciales; }
    public String[][] getJardin() { return jardin; }
}