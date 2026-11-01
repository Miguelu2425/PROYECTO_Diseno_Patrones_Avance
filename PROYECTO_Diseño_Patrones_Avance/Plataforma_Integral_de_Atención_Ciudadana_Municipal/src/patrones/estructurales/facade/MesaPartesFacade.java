package patrones.estructurales.facade;

import modelos.Ciudadano;
import patrones.comportamiento.observer.CambioEstado;
import patrones.comportamiento.observer.ObservadorCiudadano;
import patrones.comportamiento.observer.ObservadorConsola;
import patrones.comportamiento.observer.ObservadorEncuesta;
import patrones.comportamiento.observer.ObservadorTramite;
import patrones.comportamiento.state.SeguimientoTramite;
import patrones.creacionales.builder.Expediente;
import patrones.creacionales.factory.TramiteFactory;
import repositorios.RepositorioFactory;
import repositorios.TramiteRepositorio;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Facade: simplifica el registro coordinando Factory, Builder, Repositorio,
 * State y Observer.
 */
public class MesaPartesFacade {

    private final TramiteRepositorio repositorio;
    private final List<ObservadorTramite> observadores = new ArrayList<>();

    /**
     * Facade: constructor con inyeccion de dependencias completa.
     *
     * @param repositorio  repositorio a utilizar
     * @param observadores lista inicial de observadores
     */
    public MesaPartesFacade(TramiteRepositorio repositorio, List<ObservadorTramite> observadores) {
        this.repositorio = repositorio;
        if (observadores != null) {
            this.observadores.addAll(observadores);
        }
    }

    /**
     * Facade: constructor con repositorio inyectado y observadores por defecto
     * (ObservadorConsola, ObservadorCiudadano, ObservadorEncuesta).
     *
     * @param repositorio implementacion del repositorio a usar
     */
    public MesaPartesFacade(TramiteRepositorio repositorio) {
        this(repositorio, Arrays.asList(
                new ObservadorConsola(),
                new ObservadorCiudadano(),
                new ObservadorEncuesta()
        ));
    }

    /**
     * Facade: constructor sin parametros que usa RepositorioFactory y observadores por defecto.
     */
    public MesaPartesFacade() {
        this(RepositorioFactory.crear());
    }

    /**
     * Patron Observer: permite suscribir un nuevo observador a la Facade.
     *
     * @param observador observador a agregar
     */
    public void agregarObservador(ObservadorTramite observador) {
        if (observador != null) {
            observadores.add(observador);
        }
    }

    /**
     * Facade: orquesta la creacion del tramite, lo persiste en el repositorio
     * y notifica el evento de registro a los observadores.
     *
     * @param ciudadano   ciudadano solicitante
     * @param tipoTramite tipo de tramite ("reclamo" o "solicitud")
     * @return expediente creado con codigo unico real
     */
    public Expediente registrarNuevoTramite(Ciudadano ciudadano, String tipoTramite) {
        System.out.println("---Iniciando Mesa de Partes Virtual---");
        TramiteFactory.crearTramite(tipoTramite).procesar();
        Expediente expediente = repositorio.registrar(ciudadano, tipoTramite);
        expediente.mostrarDetalle();

        // Patron Observer: notificacion inicial de registro (null -> "En Revision")
        CambioEstado cambioInicial = new CambioEstado(
                expediente.getCodigo(),
                null,
                "En Revision",
                ciudadano.getNombre(),
                ciudadano.getCorreo(),
                tipoTramite
        );
        notificarObservadores(cambioInicial);

        return expediente;
    }

    /**
     * Patron State y Observer:
     * Avanza el estado del expediente siguiendo las transiciones oficiales.
     * Persiste primero en la base de datos; si tiene exito, avanza el SeguimientoTramite
     * y notifica a los observadores.
     *
     * @param codigo codigo unico del expediente
     * @return expediente actualizado
     */
    public Expediente avanzarEstado(String codigo) {
        Expediente expediente = repositorio.buscarPorCodigo(codigo);
        SeguimientoTramite seguimiento = new SeguimientoTramite(expediente);
        for (ObservadorTramite obs : observadores) {
            seguimiento.agregarObservador(obs);
        }

        if (seguimiento.getEstado().esFinal()) {
            seguimiento.avanzar(); // Imprime "Mensaje: el tramite <codigo> ya esta Cerrado"
            return expediente;
        }

        String proximo = seguimiento.proximoEstado().getNombre();
        Expediente actualizado = repositorio.cambiarEstado(codigo, proximo);
        seguimiento.avanzar(); // Realiza la transicion y notifica
        return actualizado;
    }

    /**
     * Facade: delega la consulta de tramites al repositorio activo.
     */
    public List<Expediente> listarTramites(String dniOpcional) {
        return repositorio.listar(dniOpcional);
    }

    /**
     * Facade: delega el cambio de estado directo al repositorio activo.
     */
    public Expediente cambiarEstado(String codigo, String estadoNuevo) {
        return repositorio.cambiarEstado(codigo, estadoNuevo);
    }

    /**
     * Facade: describe el origen del repositorio activo.
     */
    public String descripcionRepositorio() {
        return repositorio.descripcionOrigen();
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