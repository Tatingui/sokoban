package utilidades;

import modelo.*;
import java.util.HashMap;
import java.util.Arrays;
import java.util.Map;
import java.util.function.Supplier;

public class FabricaDeEntidades {

    private static FabricaDeEntidades instancia;
    private final Map<String, Supplier<Entidad>> registro;

    private FabricaDeEntidades() {
        registro = new HashMap<>();

        // Estáticas simples
        registro.put("P",  Pared::new);
        registro.put("S",  SueloNormal::new);

        // Dinámicas simples
        registro.put("J",  Sokoban::new);
        registro.put("C",  CajaNormal::new);

        // Con parámetros — usamos lambdas
        registro.put("F",  () -> new CajaFragil(3));

        // Muros cerrados y cerrojos se parsean aparte
        // porque llevan canal en el token (ej: "MC-AZUL", "X-ROJO-EXC")
    }

    public static FabricaDeEntidades getInstancia() {
        if (instancia == null) {
            instancia = new FabricaDeEntidades();
        }
        return instancia;
    }

    public Entidad crearEntidad(String token) {
        // Intenta parsear tokens compuestos con canal
        Entidad entidad = ParserDeToken.parsear(token);
        if (entidad != null) return entidad;
    
        // Token simple: busca en el registro
        Supplier<Entidad> constructor = registro.get(token);
        if (constructor != null) return constructor.get();
    
        System.out.println("Token desconocido: '" + token + "', se reemplaza por SueloNormal.");
        return new SueloNormal();
    }
}