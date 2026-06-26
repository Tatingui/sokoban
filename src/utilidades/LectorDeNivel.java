// LectorDeNivel.java
package utilidades;

import modelo.Tablero;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lee un archivo de nivel (.txt) y delega la construcción del {@link Tablero}
 * en el {@link DirectorDeTablero} (patrón Builder). Su única responsabilidad es
 * la lectura/parseo del archivo a filas de tokens; el armado del objeto complejo
 * queda en el Director + ConstructorTablero.
 */
public class LectorDeNivel {

    public Tablero cargarNivel(String rutaArchivo) {
        List<String[]> filas = leerFilas(rutaArchivo);
        if (filas == null || filas.isEmpty()) {
            return null;
        }
        DirectorDeTablero director = new DirectorDeTablero(new ConstructorTablero());
        return director.construir(filas);
    }

    /** Lee el archivo y lo convierte en filas de tokens (separados por espacios). */
    private List<String[]> leerFilas(String rutaArchivo) {
        List<String[]> filas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank()) {
                    filas.add(linea.trim().split(" "));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el nivel: " + e.getMessage());
            return null;
        }
        return filas;
    }
}
