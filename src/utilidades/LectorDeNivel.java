// LectorDeNivel.java
package utilidades;

import modelo.Tablero;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorDeNivel {

    public Tablero cargarNivel(String rutaArchivo) {
        List<String[]> filas = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (!linea.isBlank()) {
                    // Cada celda es un token separado por espacio
                    filas.add(linea.trim().split(" "));
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el nivel: " + e.getMessage());
            return null;
        }

        if (filas.isEmpty()) return null;

        int numFilas = filas.size();
        int numColumnas = filas.get(0).length;

        ConstructorTablero constructor = new ConstructorTablero();
        constructor.definirTamanio(numFilas, numColumnas);

        for (int i = 0; i < numFilas; i++) {
            constructor.procesarFila(i, filas.get(i));
        }

        return constructor.obtenerTablero();
    }
}