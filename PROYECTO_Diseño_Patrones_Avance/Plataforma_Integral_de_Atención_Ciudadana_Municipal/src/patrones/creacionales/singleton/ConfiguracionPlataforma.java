package patrones.creacionales.singleton;

/**
 * Singleton: mantiene una única configuración global de la plataforma.
 */
public class ConfiguracionPlataforma {

    private static ConfiguracionPlataforma instancia;
    private String nombreMunicipio;

    /**
     * Singleton: evita que otras clases creen configuraciones adicionales.
     */
    private ConfiguracionPlataforma() {
        this.nombreMunicipio = "Municipalidad de Lima Norte";
    }

    /**
     * Singleton: entrega la única instancia compartida de configuración.
     */
    public static ConfiguracionPlataforma getInstancia() {
        if (instancia == null) {
            instancia = new ConfiguracionPlataforma();
        }
        return instancia;
    }

    /**
     * Singleton: consulta el nombre municipal almacenado globalmente.
     */
    public String getNombreMunicipio() {
        return nombreMunicipio;
    }

}
