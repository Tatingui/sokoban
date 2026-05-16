package modelo;

public class Caja implements ElementoTablero {
    @Override
    public boolean esTransitable() {
        return false; // No podés "caminar por encima" de una caja libremente como si fuera suelo; ocupa un lugar físico.
    }
}