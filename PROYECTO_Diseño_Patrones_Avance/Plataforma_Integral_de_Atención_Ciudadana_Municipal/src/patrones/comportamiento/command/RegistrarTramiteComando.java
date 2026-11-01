package patrones.comportamiento.command;

import modelos.Ciudadano;
import patrones.comportamiento.observer.CambioEstado;
import patrones.comportamiento.observer.ObservadorTramite;
import patrones.creacionales.builder.Expediente;
import patrones.creacionales.factory.TramiteFactory;
import repositorios.TramiteRepositorio;

import java.util.ArrayList;
import java.util.List;

/**
 * Patrón Command: encapsula la acción de registrar un nuevo trámite municipal.
 * Permite ejecutar el registro orquestando Factory, Builder y Repositorio,
 * y revertirlo (deshacer) eliminando el expediente creado si permanece en estado inicial.
 */
public class RegistrarTramiteComando implements Comando {

    private final TramiteRepositorio repositorio;
    private final Ciudadano ciudadano;
    private final String tipoTramite;
    private final List<ObservadorTramite> observadores = new ArrayList<>();
    private Expediente expedienteCreado;

    /**
     * Patrón Command: constructor del comando con sus dependencias y datos requeridos.
     *
     * @param repositorio  repositorio donde persistir el trámite
     * @param ciudadano    ciudadano solicitante
     * @param tipoTramite  tipo de trámite ("Reclamo" o "Solicitud")
     * @param observadores lista de observadores a notificar
     */
    public RegistrarTramiteComando(TramiteRepositorio repositorio,
                                  Ciudadano ciudadano,
                                  String tipoTramite,
                                  List<ObservadorTramite> observadores) {
        this.repositorio = repositorio;
        this.ciudadano = ciudadano;
        this.tipoTramite = tipoTramite;
        if (observadores != null) {
            this.observadores.addAll(observadores);
        }
    }

    /**
     * Patrón Command: ejecuta el procesamiento de la fábrica, persiste el expediente
     * en el repositorio y notifica el evento de registro inicial a los observadores.
     */
    @Override
    public void ejecutar() {
        System.out.println("---Iniciando Mesa de Partes Virtual---");
        TramiteFactory.crearTramite(tipoTramite).procesar();
        this.expedienteCreado = repositorio.registrar(ciudadano, tipoTramite);
        this.expedienteCreado.mostrarDetalle();

        // Patron Observer: notificacion inicial de registro (null -> "En Revision")
        CambioEstado cambioInicial = new CambioEstado(
                expedienteCreado.getCodigo(),
                null,
                "En Revision",
                ciudadano.getNombre(),
                ciudadano.getCorreo(),
                tipoTramite
        );
        notificarObservadores(cambioInicial);
    }

    /**
     * Patrón Command: deshace el registro eliminando el expediente del repositorio.
     * Solo tiene éxito si el trámite continúa en estado inicial 'En Revision'.
     */
    @Override
    public void deshacer() {
        if (expedienteCreado == null) {
            throw new IllegalStateException("No se puede deshacer un comando no ejecutado.");
        }
        repositorio.eliminar(expedienteCreado.getCodigo());
        System.out.println("[Command] Registro deshecho: " + expedienteCreado.getCodigo() + " eliminado");
    }

    /**
     * Patrón Command: retorna la descripción de la acción.
     */
    @Override
    public String getDescripcion() {
        if (expedienteCreado != null) {
            return "Registrar tramite " + expedienteCreado.getCodigo() + " (" + tipoTramite + ")";
        }
        return "Registrar tramite (" + tipoTramite + ")";
    }

    /**
     * Obtiene el expediente creado por la ejecución del comando.
     *
     * @return expediente creado o null si no se ha ejecutado
     */
    public Expediente getExpedienteCreado() {
        return expedienteCreado;
    }

    private void notificarObservadores(CambioEstado cambio) {
        for (ObservadorTramite o : observadores) {
            try {
                o.actualizar(cambio);
            } catch (Exception ex) {
                System.err.println("Advertencia: observador fallo: " + ex.getMessage());
            }
        }
    }
}
