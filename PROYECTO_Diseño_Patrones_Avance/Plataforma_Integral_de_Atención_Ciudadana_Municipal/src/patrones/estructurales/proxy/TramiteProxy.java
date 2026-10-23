package patrones.estructurales.proxy;

import patrones.creacionales.factory.TramiteFactory;
import patrones.creacionales.productos.Tramite;

/**
 * Proxy: valida autorización y crea Tramite real solo cuando se necesita.
 */
public class TramiteProxy implements Tramite {

    private final String tipoTramite;
    private final boolean ciudadanoAutorizado;
    private Tramite tramiteReal;

    /**
     * Proxy: guarda el tipo y el permiso sin crear todavía el Subject real.
     */
    public TramiteProxy(String tipoTramite, boolean ciudadanoAutorizado) {
        this.tipoTramite = tipoTramite;
        this.ciudadanoAutorizado = ciudadanoAutorizado;
    }

    @Override
    /**
     * Proxy: valida el permiso antes de delegar al Subject real.
     */
    public void procesar() {
        if (!ciudadanoAutorizado) {
            System.out.println("Acceso denegado: el ciudadano no esta autorizado.");
            return;
        }

        if (tramiteReal == null) {
            System.out.println("Proxy: validacion exitosa. Creando tramite real...");
            tramiteReal = TramiteFactory.crearTramite(tipoTramite);
        }
        tramiteReal.procesar();
    }
}
