package modelo;

public abstract class SueloEspecial extends EntidadEstatica {
    private EntidadDinamica objetoEncima;

    public EntidadDinamica getObjetoEncima() { return objetoEncima; }
    public void setObjetoEncima(EntidadDinamica objeto) { this.objetoEncima = objeto; }
    public boolean estaOcupado() { return objetoEncima != null; }

    @Override
    public boolean esTransitable() {
        return !estaOcupado();
    }

    @Override
    public boolean aceptarIngreso(Tablero tablero, int fila, int col, Direccion direccion) {
        if (!estaOcupado()) {
            return true; // Si está libre, se puede pisar
        }
        // Tell, Don't Ask: Delega polimórficamente la decisión a la entidad que tiene encima
        return objetoEncima.acceptarIngreso(tablero, fila, col, direccion);
    }
}

