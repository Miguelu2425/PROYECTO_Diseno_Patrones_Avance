package patrones.comportamiento.observer;

/**
 * Patron Observer: evento inmutable con los datos del cambio de estado.
 */
public class CambioEstado {

    private final String codigo;
    private final String estadoAnterior;
    private final String estadoNuevo;
    private final String nombreCiudadano;
    private final String correo;
    private final String tipoTramite;

    public CambioEstado(String codigo, String estadoAnterior, String estadoNuevo,
                        String nombreCiudadano, String correo, String tipoTramite) {
        this.codigo = codigo;
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.nombreCiudadano = nombreCiudadano;
        this.correo = correo;
        this.tipoTramite = tipoTramite;
    }

    public String getCodigo() {
        return codigo;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public String getNombreCiudadano() {
        return nombreCiudadano;
    }

    public String getCorreo() {
        return correo;
    }

    public String getTipoTramite() {
        return tipoTramite;
    }
}