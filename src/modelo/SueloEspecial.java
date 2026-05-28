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
}