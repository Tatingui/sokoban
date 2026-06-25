// utilidades/ParserDeToken.java
package utilidades;

import modelo.*;

public class ParserDeToken {

    public static Entidad parsear(String token) {
        // Muro abierto (A-CANAL): nace transitable.
        if (token.startsWith("A-"))
            return new Muro(token.substring(2), true);

        // Muro cerrado: nuevo formato M-CANAL y legado MC-CANAL.
        if (token.startsWith("MC-"))
            return new Muro(token.substring(3));
        if (token.startsWith("M-"))
            return new Muro(token.substring(2));

        if (token.startsWith("X-"))
            return new CasilleroCerrojo(token.substring(2));

        if (token.startsWith("L-")) {
            String canal = token.substring(2);
            return canal.equals("MULTI") ? CajaLlave.comodin() : CajaLlave.deCanal(canal);
        }

        return null; // Token simple, que lo resuelva la fábrica
    }
}