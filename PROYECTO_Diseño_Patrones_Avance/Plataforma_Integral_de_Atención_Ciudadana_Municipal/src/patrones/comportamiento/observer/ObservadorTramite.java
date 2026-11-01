package patrones.comportamiento.observer;

/**
 * Patron Observer: contrato de los observadores interesados en cambios de tramite.
 */
public interface ObservadorTramite {

    /**
     * Patron Observer: actualiza el observador con el cambio de estado acontecido.
     *
     * @param cambio datos del cambio de estado
     */
    void actualizar(CambioEstado cambio);
}