package patrones.comportamiento.state;

/**
 * Patron State: interfaz que define el contrato y transiciones de estado de un tramite.
 */
public interface EstadoTramite {

    /**
     * Patron State: devuelve el nombre oficial del estado ("En Revision", "Atendido", "Cerrado").
     *
     * @return nombre oficial del estado
     */
    String getNombre();

    /**
     * Patron State: descripcion legible de la fase actual del tramite.
     *
     * @return descripcion de la fase
     */
    String getDescripcion();

    /**
     * Patron State: calcula la siguiente transicion en el ciclo de vida del tramite.
     *
     * @return proximo estado
     */
    EstadoTramite siguiente();

    /**
     * Patron State: indica si este estado es el terminal del ciclo de vida.
     *
     * @return true si es estado final, false si admite transiciones
     */
    boolean esFinal();
}