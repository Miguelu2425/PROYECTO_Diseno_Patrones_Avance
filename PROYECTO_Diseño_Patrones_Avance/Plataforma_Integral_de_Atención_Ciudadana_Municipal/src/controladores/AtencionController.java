package controladores;

import modelos.Ciudadano;
import patrones.creacionales.builder.Expediente;
import patrones.estructurales.facade.MesaPartesFacade;

import java.util.List;

/**
 * Controlador MVC: conecta la vista con la Facade de mesa de partes.
 * GRASP Controlador: es el primer objeto que recibe las solicitudes de la vista.
 */
public class AtencionController {

    private final MesaPartesFacade mesaPartes;

    /**
     * Controlador: inicializa la Facade que coordina el caso de uso.
     */
    public AtencionController() {
        this.mesaPartes = new MesaPartesFacade();
    }

    /**
     * Controlador: recibe la solicitud de la vista y delega el registro a la Facade.
     *
     * @return expediente creado con codigo unico real
     */
    public Expediente atenderCiudadano(String dni, String nombre, String correo, String tramite) {
        Ciudadano ciudadano = new Ciudadano(dni, nombre, correo);
        return mesaPartes.registrarNuevoTramite(ciudadano, tramite);
    }

    /**
     * Patron State y Observer: avanza el estado del tramite delegando en la Facade.
     *
     * @param codigo codigo unico del expediente
     * @return expediente actualizado
     */
    public Expediente avanzarEstado(String codigo) {
        return mesaPartes.avanzarEstado(codigo);
    }

    /**
     * Controlador: lista los tramites del ciudadano con el DNI indicado.
     *
     * @param dni DNI del ciudadano o null para listar todos
     * @return lista de expedientes
     */
    public List<Expediente> listarMisSolicitudes(String dni) {
        return mesaPartes.listarTramites(dni);
    }

    /**
     * Controlador: informa el origen del repositorio activo para mostrarlo en la vista.
     *
     * @return descripcion del origen de persistencia
     */
    public String estadoConexion() {
        return mesaPartes.descripcionRepositorio();
    }

    /**
     * Patron Command: deshace la ultima accion ejecutada (registro o cambio de estado).
     *
     * @return descripcion de la accion deshecha o null si no habia acciones
     */
    public String deshacerUltimaAccion() {
        return mesaPartes.deshacerUltimaAccion();
    }

    /**
     * Patron Command: consulta si hay acciones en la pila listas para deshacer.
     *
     * @return true si se puede deshacer; false si la pila esta vacia
     */
    public boolean puedeDeshacer() {
        return mesaPartes.puedeDeshacer();
    }

    /**
     * Patron Command: obtiene la descripcion de la ultima accion que se puede deshacer.
     *
     * @return descripcion de la accion o null si no hay ninguna
     */
    public String descripcionUltimaAccion() {
        return mesaPartes.descripcionUltimaAccion();
    }

    /**
     * Patron Command: devuelve el historial de descripciones de acciones pendientes de deshacer.
     *
     * @return lista de descripciones
     */
    public List<String> historialAcciones() {
        return mesaPartes.historialAcciones();
    }
}