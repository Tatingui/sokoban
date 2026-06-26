package controlador;

/**
 * Estadísticas de un nivel completado, que el {@link ControladorJuego} entrega
 * al {@link GestorDeNiveles} para mostrarlas en la pantalla de victoria.
 */
public record EstadisticasNivel(int movimientos, int empujes, int deshacerUsados, int puntaje) {
}
