package Backend.Cuentas;

import Backend.Extructuras.ListaDinamica;
// es para leer lineas de codigo completa
import java.io.BufferedReader;
//Es para escribir lineas de codigos completas
import java.io.BufferedWriter;
// permite representar rutas y directoriso
import java.io.File;
// permite leer flujos de caracteres
import java.io.FileReader;
// escribe flujos de caracteres
import java.io.FileWriter;
// maneja los errores de entrada y de salida
import java.io.IOException;

public class ManejadorCuentas {

    // Es una constante que se usa para saber donde se guardaran los datos del objeto
    private static final String DATOS_CUENTA = "cuentas";
    // Variable que almacena los datos de la cuenta
    private ListaDinamica<Cuenta> cuentas;

    // constructor
    public ManejadorCuentas() {
        // se genera una lista dinamica nueva
        this.cuentas = new ListaDinamica<>();
        File directorio = new File(DATOS_CUENTA);
        // si la el dierctorio no existe llamamos al metodo de mkdirs que crea directorios
        if (!directorio.exists()) {
            directorio.mkdirs();
        }
        // llamamos al metodo para cargar cuentas
        cargarCuentas();
    }

    // Validamos si el nombre es correcto
    public String validarNombre(String nombre) {

        // si no existe un nombre entonces pide el nombre
        if (nombre == null) {
            return "Tienes que colocar un nombre a la partida";
        }

        // Eliminamos los espacios enblanco que dejamos en el nombre
        nombre = nombre.trim();

        // Comprobamos que el nombre tenga al menos una letra y maximo 12
        if (nombre.length() < 1 || nombre.length() > 12) {
            return "El nombre debe tener entre 1 y 12 caracteres.";
        }

        // Quitamos los caracteres especiales
        for (int i = 0; i < nombre.length(); i++) {
            // Descomponemos el nombre por caracteres
            char c = nombre.charAt(i);
            // si el caracter es distinto a letras o numeros
            if (!Character.isLetterOrDigit(c)) {
                return "El nombre solo puede ser Letras y Numeros";
            }
        }

        // Compara que no exista una cuenta con ese nombre
        for (int i = 0; i < cuentas.getTamaño(); i++) {
            // si la cuenta ya existe
            if (cuentas.obtener(i).getNombrePartida().equals(nombre)) {
                return "Ya existe una cuenta registrada con este nombre.";
            }
        }

        // Si todos los parametrso estan bien retorna la cuetna
        return null;
    }

    // Crear nueva cuenta, su carpeta de partidas y archivo stats.txt
    public boolean crearCuenta(String nick) {
        // si la cuenta no se creo bien no hace nada y retorna
        String error = validarNombre(nick);
        if (error != null) {
            return false;
        }

        // Eliminamos los espacios en blanco del nombre, tanto como adelante como atras
        String nicknameLimpio = nick.trim();
        // Creamos un objeto en el cual se guardaran los nombres de las cuentas
        File carpetaUsuario = new File(DATOS_CUENTA, nicknameLimpio);
        // Dejamos el directorio en el cual se guardaran los archivos
        File carpetaPartidas = new File(carpetaUsuario, "partidas");

        // si se crea la carpeta de las partidas
        if (carpetaPartidas.mkdirs()) {
            // se crea un objeto de la partida pero con el nombre ya limpio
            Cuenta nueva = new Cuenta(nicknameLimpio);
            cuentas.agregar(nueva);
            // se reinician las estadisticas
            guardarEstadisticasTxt(nueva);
            return true;
        }
        return false;
    }

    // Obtiene todas las partidas guardadas de todas las carpetas de cuentas automáticamente
    public String[] obtenerTodasLasPartidas() {
        // Se crea un archivo que contenga el directorio
        File directorioBase = new File(DATOS_CUENTA);
        // si no existe un directorio base o si la ruta no es un directorio
        if (!directorioBase.exists() || !directorioBase.isDirectory()) {
            // retorna un arreglo con 0 elementos así no se retorna falso
            return new String[0];
        }
        /*
        / List File Obiene entradas que cumplan con lo que pide
        / File isDirectory es una referencia que sirve como filtro
        */

        // Obtiene los objetos que usan las referencias
        File[] carpetasCuentas = directorioBase.listFiles(File::isDirectory);
        // si hay un error entoces retorna un arreglo 0
        if (carpetasCuentas == null) return new String[0];

        // se crea un contador el cual indica cuantos archivos hemos creado
        int totalArchivos = 0;
        // for que recorre las carpetas de cuentas
        for (File cuentaDirectorio : carpetasCuentas) {
            // Se crea un archivo el cual recibira la direccion de la carpeta donde se guardan las partidas
            File carpetaPartidas = new File(cuentaDirectorio, "partidas");
            // si la carpeta existe y si el directorio existe
            if (carpetaPartidas.exists() && carpetaPartidas.isDirectory()) {
                // obienen las entradas de los archivos
                File[] archivos = carpetaPartidas.listFiles();
                // si los archivos no existen
                if (archivos != null) {
                    // se llama un for que recorre cada elemento de los archivos
                    for (File f : archivos) {
                        // si el archivo existe entonces aumenta el contador de archivos
                        if (f.isFile()) totalArchivos++;
                    }
                }
            }
        }

        // si el contador es 0 entonces retornamos un arreglo 0
        if (totalArchivos == 0) return new String[0];

        // Se crea el arreglo el cual tendra el tamaño exacto que se calculo con el contador
        String[] partidasEncontradas = new String[totalArchivos];
        // se declara un indice
        int indice = 0;
        // recorre las carpetas
        for (File cuentaDir : carpetasCuentas) {
            // Se crea un archivo el cual recibira la direccion de la carpeta donde se guardan las partidas
            File carpetaPartidas = new File(cuentaDir, "partidas");
            // si la carpeta y el directorio existen
            if (carpetaPartidas.exists() && carpetaPartidas.isDirectory()) {
                //  obienen las entradas de los archivos
                File[] archivos = carpetaPartidas.listFiles();
                if (archivos != null) {
                    //se llama un for que recorre cada elemento de los archivos
                    for (File f : archivos) {
                        // si el archivo existe
                        if (f.isFile()) {
                            // Guarda el formato "NombreCuenta/NombrePartida.txt"
                            partidasEncontradas[indice++] = cuentaDir.getName() + "/" + f.getName();
                        }
                    }
                }
            }
        }

        return partidasEncontradas;
    }

    // Cargar todas las cuentas leyendo sus archivos estadisticas
    public void cargarCuentas() {
        // se crea una lista dinamica
        cuentas = new ListaDinamica<>();

        // se crea un archivo que representa al directorio en el cual se guardaran los datos
        File directorioBase = new File(DATOS_CUENTA);
        // Se crea un arreglo en el cual recie los archivos sin filtros
        File[] carpetas = directorioBase.listFiles();

        // si la carpeta es nula retorna
        if (carpetas == null) return;

        // recorre las carpetas
        for (File carpeta : carpetas) {
            // si en la entrada es un directorio
            if (carpeta.isDirectory()) {
                // Construye una ruta
                File archivoEstadisticas = new File(carpeta, "Estadisticas.txt");
                // si el archivo existe
                if (archivoEstadisticas.exists()) {
                    // llama al metodo que lee las estadisticas
                    Cuenta cuenta = cargarEstadisticasTxt(carpeta.getName(), archivoEstadisticas);
                    // si la cuenta diferente a nula entonces crea la cuenta
                    if (cuenta != null) {
                        cuentas.agregar(cuenta);
                    }
                }
            }
        }
    }

    // Carga las métricas desde el archivo Estadisticas.txt plano
    private Cuenta cargarEstadisticasTxt(String nombre, File archivo) {
        // se crea un objeto que recibe el nombre
        Cuenta cuenta = new Cuenta(nombre);
        // el try se encarga que cuando termine de usar el BufferReader se cierre
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            // se crea una variable que representan la linea que se esta leyendo
            String linea;
            // Leemos líneas hasta llegar al final del archivo
            while ((linea = br.readLine()) != null) {
                // se usa la linea en partes usando el igual para separarlos
                String[] partes = linea.split("=");
                // si la particion es a la mitad
                if (partes.length == 2) {
                    // se usa la primera parte y elimina los espacios en blanco
                    String clave = partes[0].trim();

                    // Convertimos el valor de texto a un número entero
                    int valor = Integer.parseInt(partes[1].trim());

                    switch (clave) {
                        case "partidasJugadas" -> cuenta.setPartidasJugadas(valor);
                        case "partidasGanadas" -> cuenta.setPartidasGanadas(valor);
                        case "partidasPerdidas" -> cuenta.setPartidasPerdidas(valor);
                        case "plantasDestruidas" -> cuenta.setPlantasDestruidas(valor);
                        case "zombisInvocados" -> cuenta.setZombisInvocados(valor);
                        case "cerebrosGastados" -> cuenta.setCerebrosGastados(valor);
                        case "mejorTiempoVictoria" -> cuenta.setMejorTiempoVictoria(valor);
                        case "podadorasActivadas" -> cuenta.setPodadorasActivadas(valor);
                    }
                }
            }
            return cuenta;
            // Si hay un error al cargar los datos entoncs
        } catch (Exception e) {
            // se imprime el error
            System.err.println("Error al cargar la estadistica de " + nombre + ": " + e.getMessage());
            return null;
        }
    }

    // Guardar estadísticas en archivo de texto plano (.txt) por cuenta
    public void guardarEstadisticasTxt(Cuenta cuenta) {
        // construye una ruta para el archivo
        File archivo = new File(DATOS_CUENTA + "/" + cuenta.getNombrePartida(), "Estadisticas.txt");
        // Abrimos el archivo para escribir estadísticas en formato de texto
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivo))) {
            bw.write("partidasJugadas=" + cuenta.getPartidasJugadas());
            bw.newLine();
            bw.write("partidasGanadas=" + cuenta.getPartidasGanadas());
            bw.newLine();
            bw.write("partidasPerdidas=" + cuenta.getPartidasPerdidas());
            bw.newLine();
            bw.write("plantasDestruidas=" + cuenta.getPlantasDestruidas());
            bw.newLine();
            bw.write("zombisInvocados=" + cuenta.getZombisInvocados());
            bw.newLine();
            bw.write("cerebrosGastados=" + cuenta.getCerebrosGastados());
            bw.newLine();
            bw.write("mejorTiempoVictoria=" + cuenta.getMejorTiempoVictoria());
            bw.newLine();
            bw.write("podadorasActivadas=" + cuenta.getPodadorasActivadas());
            // try por en caso de que exista un error de escritura
        } catch (IOException e) {
            System.err.println("Error al guardar la estadistica de " + cuenta.getNombrePartida() + ": " + e.getMessage());
        }
    }

    // Guarda o crea un archivo de partida nueva en la carpeta del usuario
    public boolean guardarPartida(String nombre, String nombreArchivo, String contenidoPartida) {
        // Asegurarse de que la extensión sea válida (por ejemplo, .txt o .json)
        if (!nombreArchivo.endsWith(".txt") && !nombreArchivo.endsWith(".json")) {
            nombreArchivo += ".txt";
        }

        File carpetaPartidas = new File(DATOS_CUENTA + "/" + nombre + "/partidas");
        if (!carpetaPartidas.exists()) {
            carpetaPartidas.mkdirs();
        }

        File archivoPartida = new File(carpetaPartidas, nombreArchivo);

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(archivoPartida))) {
            bw.write(contenidoPartida);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar la partida " + nombreArchivo + ": " + e.getMessage());
            return false;
        }
    }

}

