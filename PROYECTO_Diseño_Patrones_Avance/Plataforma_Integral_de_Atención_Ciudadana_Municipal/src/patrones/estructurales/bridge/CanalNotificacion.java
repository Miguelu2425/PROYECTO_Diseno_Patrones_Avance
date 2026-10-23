package patrones.estructurales.bridge;

/**
 * Bridge Implementor: contrato común para los canales de notificación.
 */
public interface CanalNotificacion {

    /**
     * Implementor: define cómo un canal entrega un mensaje.
     */
    void enviar(String mensaje);
}
