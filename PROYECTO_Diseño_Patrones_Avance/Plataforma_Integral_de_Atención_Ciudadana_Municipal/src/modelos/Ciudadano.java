package modelos;

/**
 * Modelo de datos utilizado por la Facade para registrar al ciudadano.
 */
public class Ciudadano {

    private String dni;
    private String nombre;
    private String correo;

    /**
     * Modelo: crea un ciudadano con sus datos básicos de contacto.
     */
    public Ciudadano(String dni, String nombre, String correo) {
        this.dni = dni;
        this.nombre = nombre;
        this.correo = correo;
    }

    /**
     * Modelo: devuelve el nombre del ciudadano.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Modelo: devuelve el correo usado por la Facade para notificar.
     */
    public String getCorreo() {
        return correo;
    }

}
