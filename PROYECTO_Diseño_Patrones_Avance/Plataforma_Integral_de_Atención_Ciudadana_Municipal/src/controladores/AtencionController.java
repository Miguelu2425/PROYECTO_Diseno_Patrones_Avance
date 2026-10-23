package controladores;

import modelos.Ciudadano;
import patrones.estructurales.facade.MesaPartesFacade;

/**
 * Controlador MVC: conecta la vista con la Facade de mesa de partes.
 */
public class AtencionController {

    private MesaPartesFacade mesaPartes;

    /**
     * Controlador: inicializa la Facade que coordina el caso de uso.
     */
    public AtencionController() {
        this.mesaPartes = new MesaPartesFacade();
    }

    /**
     * Controlador: recibe la solicitud de la vista y delega el registro completo.
     */
    public void atenderCiudadano(String dni, String nombre, String correo, String tramite) {
        Ciudadano ciudadano = new Ciudadano(dni, nombre, correo);
        mesaPartes.registrarNuevoTramite(ciudadano, tramite);
    }
}
