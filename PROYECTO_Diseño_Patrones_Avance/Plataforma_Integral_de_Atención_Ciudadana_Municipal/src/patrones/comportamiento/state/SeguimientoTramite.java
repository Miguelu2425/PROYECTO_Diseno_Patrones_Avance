package patrones.comportamiento.state;

import patrones.comportamiento.observer.CambioEstado;
import patrones.comportamiento.observer.ObservadorTramite;
import patrones.creacionales.builder.Expediente;

import java.util.ArrayList;
import java.util.List;

/**
 * Patron State (Contexto) y Patron Observer (Sujeto):
 * Administra el estado actual del expediente y coordina la notificacion a observadores.
 */
public class SeguimientoTramite {

    private final Expediente expediente;
    private EstadoTramite estado;
    private final List<ObservadorTramite> observadores = new ArrayList<>();

    /**
     * Patron State: inicializa el seguimiento con el estado del expediente provisto.
     *
     * @param expediente expediente a seguir
     */
    public SeguimientoTramite(Expediente expediente) {
        this.expediente = expediente;
        this.estado = EstadosTramite.desdeNombre(expediente.getEstado());
    }

    /**
     * Patron State: devuelve el estado actual.
     *
     * @return estado actual
     */
    public EstadoTramite getEstado() {
        return estado;
    }

    /**
     * Patron State: consulta el siguiente estado sin modificar el estado actual.
     *
     * @return siguiente estado segun el patron State
     */
    public EstadoTramite proximoEstado() {
        return estado.siguiente();
    }

    /**
     * Patron Observer: registra un observador interesado en cambios de estado.
     *
     * @param o observador a agregar
     */
    public void agregarObservador(ObservadorTramite o) {
        if (o != null) {
            observadores.add(o);
        }
    }

    /**
     * Patron State y Observer:
     * Si el estado es final, imprime el aviso y retorna false.
     * Si no, cambia al siguiente estado, notifica a todos los observadores y retorna true.
     * Captura cualquier excepcion en un observador para asegurar que los demas se notifiquen.
     *
     * @return true si la transicion ocurrio; false si el tramite ya estaba Cerrado
     */
    public boolean avanzar() {
        if (estado.esFinal()) {
            System.out.println("Mensaje: el tramite " + expediente.getCodigo() + " ya esta Cerrado");
            return false;
        }

        EstadoTramite anterior = this.estado;
        this.estado = this.estado.siguiente();

        CambioEstado cambio = new CambioEstado(
                expediente.getCodigo(),
                anterior.getNombre(),
                this.estado.getNombre(),
                expediente.getNombreCiudadano(),
                expediente.getCorreo(),
                expediente.getTipoDocumento()
        );

        for (ObservadorTramite o : observadores) {
            try {
                o.actualizar(cambio);
            } catch (Exception ex) {
                System.err.println("Advertencia: observador fallo: " + ex.getMessage());
            }
        }
        return true;
    }
}