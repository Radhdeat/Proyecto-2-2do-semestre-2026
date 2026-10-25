package Backend.Extructuras;

// Usamos T que indica que es un tipo generico para almacenar diferentes tipos de objetos
public class ListaDinamica<T> {

    // Atributos
    private Object[] elementos;
    private int tamaño;
    private static final int CAPACIDAD_INICIAL = 10;

    // constructor
    public ListaDinamica() {
        this.elementos = new Object[CAPACIDAD_INICIAL];
        this.tamaño = 0;
    }

    // Agrega un elemento al final de la lista. Si el arreglo se llena, duplica su capacidad.
    public void agregar(T elemento) {
        // si los elementos estan llenos
        if (tamaño == elementos.length) {
            // llama al metodo para redimensionar
            redimensionar();
        }

        elementos[tamaño] = elemento;
        tamaño++;
    }

    // indica al compilador que suprima las advertencias de conversiones
    @SuppressWarnings("unchecked")

    public T obtener(int indice) {
        // si esta dentro de los limites
        if (indice < 0 || indice >= tamaño) {
            // throw es una ecepcion que indica que esta fuera de los limites
            throw new IndexOutOfBoundsException("Índice fuera de límites: " + indice);
        }
        return (T) elementos[indice];
    }

    // Elimina un elemento por su índice y desplaza los demás a la izquierda
    public void eliminar(int indice) {
        // que este dentro de los limites
        if (indice < 0 || indice >= tamaño) {
            // si es mas grande entonces llama a la ecepcion
            throw new IndexOutOfBoundsException("Índice fuera de límites: " + indice);
        }
        // recorre el arreglo pero ojo restamos uno a tamaño asi solo se consideran
        // hasta el penultimo elemento
        for (int i = indice; i < tamaño - 1; i++) {
            // ahora el elemento 1 pasaria a ser el 2
            elementos[i] = elementos[i + 1];
        }
        // se quita la referencia del ultimo termino
        elementos[tamaño - 1] = null;
        tamaño--;
    }

    // Retorna la cantidad actual de elementos guardados
    public int getTamaño() {
        return tamaño;
    }

    // metodo para comprovar si esta vacia
    public boolean estaVacia() {
        return tamaño == 0;
    }

    // Método privado auxiliar para duplicar el tamaño del arreglo interno cuando se llena
    private void redimensionar() {
        // se crea otro arreglo pero con el doble de espacio
        Object[] nuevoArreglo = new Object[elementos.length * 2];
        // recorre el arreglo de elementos
        for (int i = 0; i < elementos.length; i++) {
            // y copia lso datos en el nuevo arreglo
            nuevoArreglo[i] = elementos[i];
        }
        elementos = nuevoArreglo;
    }
}