package patrones.estructurales.decorator;

/**
 * Component: contrato base del patrón Decorator de notificaciones.
 */
public interface Notificador {

    /**
     * Component: define el envío que pueden ampliar los decoradores.
     */
    void enviar(String mensaje);
}
