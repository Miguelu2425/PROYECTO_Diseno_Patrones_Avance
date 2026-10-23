package patrones.creacionales.factory;

import patrones.creacionales.productos.Reclamo;
import patrones.creacionales.productos.Solicitud;
import patrones.creacionales.productos.Tramite;

/**
 * Factory Method simple: centraliza la creación por tipo de trámite.
 */
public class TramiteFactory {

    /**
     * Factory: selecciona y construye el producto concreto solicitado.
     */
    public static Tramite crearTramite(String tipo) {
        if (tipo.equalsIgnoreCase("reclamo")) {
            return new Reclamo();
        }
        if (tipo.equalsIgnoreCase("solicitud")) {
            return new Solicitud();
        }
        throw new IllegalArgumentException("Tipo de tramite desconocido");
    }
}
