package utilidades;

import modelo.ElementoTablero;

public class FabricaDeEntidades {
    private static FabricaDeEntidades instancia;

    // Constructor privado para evitar que usen "new" fuera de esta clase
    private FabricaDeEntidades() {}

    // Punto de acceso global al Singleton
    public static FabricaDeEntidades getInstancia() {
        if (instancia == null) {
            instancia = new FabricaDeEntidades();
        }
        return instancia;
    }

    // Método que usará el lector de mapas más adelante
    public ElementoTablero crearEntidad(char caracter) {
        // Acá irá el switch/if-else que creará las paredes, cajas, etc.
        return null;
    }
}