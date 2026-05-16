import modelo.ElementoTablero;
import modelo.Tablero;
import utilidades.LectorDeNivel;

public class Main {
    public static void main(String[] args) {
        LectorDeNivel lector = new LectorDeNivel();

        // Le pasamos la ruta de nuestro txt
        Tablero nivelPrueba = lector.cargarNivel("niveles/nivel1.txt");

        if (nivelPrueba != null) {
            System.out.println("¡El nivel se cargó correctamente usando Factory Method, Singleton y Builder!");
            ElementoTablero[][] grilla = nivelPrueba.getGrilla();

            // Imprimimos la matriz en consola para verificar
            for (int i = 0; i < grilla.length; i++) {
                for (int j = 0; j < grilla[i].length; j++) {
                    System.out.print(grilla[i][j].getClass().getSimpleName().charAt(0) + " ");
                }
                System.out.println();
            }
        }
    }
}