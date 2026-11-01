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
     * La Facade usara RepositorioFactory para elegir BD o memoria automaticamente.
     */
    public AtencionController() {
        this.mesaPartes = new MesaPartesFacade();
    }

    /**
     * Controlador: recibe la solicitud de la vista, crea el ciudadano
     * y delega el registro completo a la Facade.
     *
     * @return expediente creado con codigo unico real
     */
    public Expediente atenderCiudadano(String dni, String nombre, String correo, String tramite) {
        Ciudadano ciudadano = new Ciudadano(dni, nombre, correo);
        return mesaPartes.registrarNuevoTramite(ciudadano, tramite);
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
     * @return texto "conectado a SQL Server mediante ConexionSingleton" o
     *         "modo memoria (sin conexion)"
     */
    public String estadoConexion() {
        return mesaPartes.descripcionRepositorio();
    }
}