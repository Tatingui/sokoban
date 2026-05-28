// utilidades/ParserDeToken.java
package utilidades;

import modelo.*;
import java.util.Arrays;

public class ParserDeToken {

    public static Entidad parsear(String token) {
        if (token.startsWith("MC-"))
            return new MuroCerrado(token.substring(3));

        if (token.startsWith("X-")) {
            boolean exclusivo = token.endsWith("-EXC");
            String canal = exclusivo
                ? token.substring(2, token.length() - 4)
                : token.substring(2);
            return new CasilleroCerrojo(canal, exclusivo);
        }

        if (token.startsWith("L-"))
            return new CajaLlave(Arrays.asList(token.substring(2).split("\\+")));

        return null; // Token simple, que lo resuelva la fábrica
    }
}