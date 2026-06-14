package utilidades;

import modelo.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class FabricaDeEntidades {

    // Singleton Thread-safe mediante Eager Initialization (feature/memento-undo)
    private static final FabricaDeEntidades INSTANCIA = new FabricaDeEntidades();

    private final Map<String, Supplier<? extends Entidad>> registro;

    private FabricaDeEntidades() {
        registro = new HashMap<>();

        // Estáticas simples
        registro.put("P",  Pared::new);
        registro.put("S",  SueloNormal::new);

        // Suelos especiales (con agregación de objetoEncima)
        registro.put("D",  Destino::new);
        registro.put("R",  SueloResbaladizo::new);

        // Dinámicas simples
        registro.put("J",  Sokoban::new);
        registro.put("C",  CajaNormal::new);

        // Con parámetros — usamos lambdas
        registro.put("F",  () -> new CajaFragil(3));
    }

    public static FabricaDeEntidades getInstancia() {
        return INSTANCIA;
    }

    public Entidad crearEntidad(String token) {
        Entidad entidad = ParserDeToken.parsear(token);
        if (entidad != null) return entidad;

        // Token simple: busca en el registro
        Supplier<? extends Entidad> constructor = registro.get(token);
        if (constructor != null) return constructor.get();

        // En vez de un sysout silencioso, lanzamos un error explícito si el mapa está roto
        throw new IllegalArgumentException(
                "Error en la configuración del mapa: El token '" + token + "' no es válido.");
    }
}
