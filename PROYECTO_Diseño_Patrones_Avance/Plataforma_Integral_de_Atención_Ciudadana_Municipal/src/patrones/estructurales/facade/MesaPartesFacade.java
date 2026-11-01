package patrones.estructurales.facade;

import modelos.Ciudadano;
import patrones.creacionales.builder.Expediente;
import patrones.creacionales.factory.TramiteFactory;
import repositorios.RepositorioFactory;
import repositorios.TramiteRepositorio;

import java.util.List;

/**
 * Facade: simplifica el registro coordinando Factory, Builder, repositorio y notificacion.
 * GRASP Controlador: centraliza el flujo del caso de uso sin exponer detalles internos.
 */
public class MesaPartesFacade {

    private final TramiteRepositorio repositorio;

    /**
     * Facade: constructor con inyeccion de dependencias (para tests y flexibilidad).
     */
    public MesaPartesFacade(TramiteRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    /**
     * Facade: constructor sin parametros que usa RepositorioFactory para elegir
     * automaticamente entre SQL Server y memoria segun disponibilidad.
     */
    public MesaPartesFacade() {
        this.repositorio = RepositorioFactory.crear();
    }

    /**
     * Facade: orquesta la creacion del tramite, lo persiste y notifica al ciudadano.
     * Factory Method: delega la creacion del tipo de tramite a TramiteFactory.
     *
     * @param ciudadano   ciudadano que solicita el tramite
     * @param tipoTramite tipo de tramite ("reclamo" o "solicitud")
     * @return expediente creado con codigo unico real
     */
    public Expediente registrarNuevoTramite(Ciudadano ciudadano, String tipoTramite) {
        System.out.println("---Iniciando Mesa de Partes Virtual---");
        TramiteFactory.crearTramite(tipoTramite).procesar();
        Expediente expediente = repositorio.registrar(ciudadano, tipoTramite);
        expediente.mostrarDetalle();
        System.out.println("Notificando al correo: " + ciudadano.getCorreo());
        return expediente;
    }

    /**
     * Facade: delega la consulta de tramites al repositorio activo.
     *
     * @param dni DNI del ciudadano o null para listar todos
     * @return lista de expedientes ordenados por fecha descendente
     */
    public List<Expediente> listarTramites(String dni) {
        return repositorio.listar(dni);
    }

    /**
     * Facade: delega el cambio de estado al repositorio activo.
     *
     * @param codigo      codigo del expediente (ej. EXP-2026-0001)
     * @param estadoNuevo nuevo estado a asignar
     * @return expediente actualizado
     */
    public Expediente cambiarEstado(String codigo, String estadoNuevo) {
        return repositorio.cambiarEstado(codigo, estadoNuevo);
    }

    /**
     * Facade: describe el origen del repositorio activo para la vista.
     *
     * @return texto descriptivo del modo de conexion
     */
    public String descripcionRepositorio() {
        return repositorio.descripcionOrigen();
    }
}