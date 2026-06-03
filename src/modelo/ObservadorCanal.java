package modelo;

/**
 * Rol Observer del patron Observer.
 * Lo implementan las entidades cuyo comportamiento depende del estado de un
 * canal de cerrojos (actualmente {@link Muro}). El {@link Canal} (Subject) las
 * notifica cuando todos sus cerrojos quedan activados o cuando se cierra.
 */
public interface ObservadorCanal {
    /** El canal alcanzo el estado abierto (todos sus cerrojos activados). */
    void alAbrirCanal();

    /** El canal volvio al estado cerrado. */
    void alCerrarCanal();
}
