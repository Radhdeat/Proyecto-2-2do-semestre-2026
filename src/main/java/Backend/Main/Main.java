package Backend.Main;
// importo la carpeta fronted ya que ahi se encuentra el menu
import Frontend.Menu.VentanaPrincipal;

public class Main {
    public static void main(String[] args) {

        // LLamamos al menu
        VentanaPrincipal ventana = new VentanaPrincipal();
        // para ser mas especificos al metodo para hacer visible el menu
        ventana.setVisible(true);
      }
}