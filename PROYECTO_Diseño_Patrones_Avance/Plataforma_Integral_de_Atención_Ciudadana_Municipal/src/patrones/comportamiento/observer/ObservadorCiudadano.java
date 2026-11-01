package patrones.comportamiento.observer;

import patrones.estructurales.bridge.AlertaCiudadano;
import patrones.estructurales.bridge.CanalCorreo;
import patrones.estructurales.decorator.Notificador;
import patrones.estructurales.decorator.NotificadorBase;
import patrones.estructurales.decorator.NotificadorSMS;

/**
 * Patron Observer: notifica al ciudadano reutilizando Bridge y Decorator.
 * Bridge: canal correo con AlertaCiudadano y CanalCorreo.
 * Decorator: canal SMS con NotificadorSMS y NotificadorBase.
 */
public class ObservadorCiudadano implements ObservadorTramite {

    @Override
    public void actualizar(CambioEstado cambio) {
        String mensaje = "Su tramite " + cambio.getCodigo()
                + " paso al estado " + cambio.getEstadoNuevo();

        // Patron Bridge: AlertaCiudadano sobre CanalCorreo
        new AlertaCiudadano(new CanalCorreo()).enviar(mensaje);

        // Patron Decorator: NotificadorSMS envolviendo NotificadorBase
        Notificador notificador = new NotificadorSMS(new NotificadorBase());
        notificador.enviar(mensaje);
    }
}