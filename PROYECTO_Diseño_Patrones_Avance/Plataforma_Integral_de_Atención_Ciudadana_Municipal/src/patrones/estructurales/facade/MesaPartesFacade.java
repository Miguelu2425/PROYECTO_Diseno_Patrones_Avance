package patrones.estructurales.facade;

import patrones.creacionales.builder.Expediente;
import patrones.creacionales.factory.TramiteFactory;
import patrones.creacionales.productos.Tramite;
import modelos.Ciudadano;

/**
 * Facade: simplifica el registro coordinando Factory, Builder y notificación.
 */
public class MesaPartesFacade {

    /**
     * Facade: ofrece una operación única para registrar un trámite completo.
     */
    public void registrarNuevoTramite(Ciudadano ciudadano, String tipoTramite) {
        System.out.println("---Iniciando Mesa de Partes Virtual---");
        Tramite tramite = TramiteFactory.crearTramite(tipoTramite);
        tramite.procesar();

        Expediente expediente = new Expediente.ExpedienteBuilder()
                .setCodigo("EXP-2026-001")
                .setEstado("En Revision")
                .setTipoDocumento(tipoTramite)
                .build();
        expediente.mostrarDetalle();

        System.out.println("Notificando al correo: " + ciudadano.getCorreo());
    }
}
