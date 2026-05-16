package utilidades;

import modelo.Tablero;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LectorDeNivel {

    public Tablero cargarNivel(String rutaArchivo) {
        ConstructorTablero constructor = new ConstructorTablero();
        List<String> lineas = new ArrayList<>();

        // Leemos todo el archivo txt y guardamos las líneas
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo de nivel: " + e.getMessage());
            return null;
        }

        if (lineas.isEmpty()) {
            return null;
        }

        // Calculamos el tamaño del tablero basándonos en el txt
        int filas = lineas.size();
        int columnas = lineas.get(0).length();

        // 1. El Builder define el tamaño de la matriz
        constructor.definirTamanio(filas, columnas);

        // 2. El Builder procesa fila por fila usando la Fábrica internamente
        for (int i = 0; i < filas; i++) {
            constructor.procesarFila(i, lineas.get(i));
        }

        // 3. Devolvemos el producto complejo terminado
        return constructor.obtenerTablero();
    }
}