package utilidades;

// Importamos todas las clases del modelo para que la fábrica las conozca
import modelo.*;

public class FabricaDeEntidades {
    private static FabricaDeEntidades instancia;

    // Constructor privado (Regala el comportamiento Singleton)
    private FabricaDeEntidades() {}

    // Punto de acceso global al Singleton
    public static FabricaDeEntidades getInstancia() {
        if (instancia == null) {
            instancia = new FabricaDeEntidades();
        }
        return instancia;
    }

    /**
     * Factory Method Parametrizado
     * Recibe un caracter del archivo de texto y fabrica el objeto del modelo correspondiente.
     */
    public ElementoTablero crearEntidad(char caracter) {
        switch (caracter) {
            case 'P':
                return new Pared();
            case 'S':
                return new Suelo();
            case 'J':
                return new Jugador();
            case 'C':
                return new Caja();
            case 'D':
                return new Destino();
            default:
                // Si viene un caracter desconocido, por seguridad devolvemos suelo vacío
                return new Suelo();
        }
    }
}