package patrones.creacionales.abstractfactory;

import patrones.creacionales.productos.Tramite;
import patrones.creacionales.prototype.EncuestaSatisfaccion;

/**
 * Abstract Factory: define la familia de productos de atención ciudadana.
 */
public interface FabricaAtencion {

    /**
     * Abstract Factory: crea el trámite concreto de una familia.
     */
    Tramite crearTramite();

    /**
     * Abstract Factory: crea el producto complementario de la misma familia.
     */
    EncuestaSatisfaccion crearEncuesta();
}
