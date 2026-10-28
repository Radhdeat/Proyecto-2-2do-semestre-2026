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
        // Se crea un StringBuilder vacío para almacenar el contenido del archivo
        StringBuilder sb = new StringBuilder();
        // se lee el archivo completo
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            // Se declara una variable para almacenar cada línea del archivo
            String linea;
            // repite la lectura mientras hayan lineas
            while ((linea = br.readLine()) != null) {
                // Elimina espacios al inicio y final y agrega la línea al StringBuilder
                sb.append(linea.trim());
            }
            // por en caso de que haya un error en la lectura
        } catch (IOException e) {
            System.err.println("Error al leer el archivo JSON: " + e.getMessage());
            return false;
        }

        // Convierte el contenido del StringBuilder en una cadena de texto
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

    // Busca una propiedad del JSON y extrae su valor entero
    private int extraerEntero(String json, String clave) {

        // Agrega comillas dobles a la clave para buscarla en el JSON
        String busqueda = "\"" + clave + "\"";

        // indexOf busca la posición donde aparece la clave dentro del texto
        int posicionClave = json.indexOf(busqueda);
        if (posicionClave == -1) return -1;

        // busca : para leer despues de esa posicion
        int posDosPuntos = json.indexOf(":", posicionClave);
        if (posDosPuntos == -1) return -1;

        // crea un objeto vacaio
        StringBuilder valor = new StringBuilder();
        // Indica si ya comenzó la lectura de los dígitos del número
        boolean leyendo = false;

        // Recorre el texto JSON desde la posición posterior a los dos puntos
        for (int i = posDosPuntos + 1; i < json.length(); i++) {
            // Obtiene el carácter que se encuentra en la posición i
            char c = json.charAt(i);

            // si es un digito numerocio
            if (Character.isDigit(c)) {
                // Indica que encontró un dígito y lo agrega al valor
                leyendo = true;
                valor.append(c);
                // Termina la lectura al encontrar un carácter que no sea dígito
            } else if (leyendo) {
                break;
            }
        }

        // si no encontro un valor devuelve -1
        if (valor.length() == 0) return -1;
        // convierte el texto en entero para devolverlo
        return Integer.parseInt(valor.toString());
    }

    // encuentra el cierre exacto del arreglo contando corchetes
    private String[][] extraerJardinNativo(String json) {

        //Busca la posición donde comienza la clave "jardin".
        int posJardin = json.indexOf("\"jardin\"");
        if (posJardin == -1) return null;

        // Busca el primer corchete de apertura después de esa clave.
        int inicioCorchete = json.indexOf("[", posJardin);
        if (inicioCorchete == -1) return null;

        // Contar la profundidad de corchetes para hallar su cierre exacto
        int corchetesAbiertos = 0;
        int finCorchete = -1;

        // For que recorre Despues del corchete hasta donde esta el ultimo corchete
        for (int i = inicioCorchete; i < json.length(); i++) {
            // Obtiene el carácter que se encuentra en la posiscion i
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

        // Crea una matriz de cadenas con una cantidad definida de filas, pero sin asignar todavía el tamaño de cada fila
        String[][] temporalCarriles = new String[totalFilas][];
        int filaActual = 0;

        int i = 0;

        // Lee Lo que esta dentro de los corchetes pero discrimina los corchetes
        while (i < contenidoJardin.length()) {
            int inicioCarril = contenidoJardin.indexOf("[", i);
            if (inicioCarril == -1) break;
            int finCarril = contenidoJardin.indexOf("]", inicioCarril);
            if (finCarril == -1) break;

            // Extrae el texto de    una fila y lo separa por comas para obtener sus celdas.
            String strCarril = contenidoJardin.substring(inicioCarril + 1, finCarril);
            String[] celdas = strCarril.split(",");

            //Elimina espacios exteriores y comillas dobles del texto de cada celda
            temporalCarriles[filaActual] = new String[celdas.length];
            for (int j = 0; j < celdas.length; j++) {
                temporalCarriles[filaActual][j] = celdas[j].trim().replace("\"", "");
            }

            filaActual++;
            i = finCarril + 1;
        }

        // Verifica que todas las filas tengan la misma cantidad de columnas.
        int columnasEsperadas = temporalCarriles[0].length;
        for (String[] carril : temporalCarriles) {
            if (carril.length != columnasEsperadas) {
                System.err.println("Error: Los carriles tienen distinta cantidad de columnas[cite: 1].");
                return null;
            }
        }

        return temporalCarriles;
    }

    // Verifica si el identificador pertenece a una planta permitida o a VACIO
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

    // Devuelve la duración del nivel en segundos
    public int getDuracionSegundos() { return duracionSegundos; }
    // Devuelve la cantidad inicial de cerebros
    public int getCerebrosIniciales() { return cerebrosIniciales; }
    // Devuelve la matriz del jardín cargada desde el JSON
    public String[][] getJardin() { return jardin; }
}