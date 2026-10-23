package patrones.creacionales.singleton;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Singleton: centraliza la única conexión reutilizable con SQL Server.
 */
public final class ConexionSingleton {

    private static final String ARCHIVO_CONFIG = "config.properties";

    private static volatile ConexionSingleton instancia;
    private Connection conexion;

    private final Properties archivo = new Properties();
    private final String url;
    private final String usuario;
    private final String clave;

    /**
     * Singleton: carga la configuración de conexión sin exponer credenciales
     * dentro del código fuente.
     */
    private ConexionSingleton() {
        cargarArchivo();

        String servidor = obtenerConfiguracion("DB_SERVER", "localhost");
        String puerto = obtenerConfiguracion("DB_PORT", "1433");
        String baseDatos = obtenerConfiguracion("DB_NAME", "Municipalidad");

        this.url = "jdbc:sqlserver://" + servidor + ":" + puerto
                + ";databaseName=" + baseDatos
                + ";encrypt=true"
                + ";trustServerCertificate=true";
        this.usuario = obtenerConfiguracion("DB_USER", "");
        this.clave = obtenerConfiguracion("DB_PASSWORD", "");
    }

    /**
     * Singleton: devuelve la instancia única de conexión de forma segura
     * cuando la aplicación es utilizada por varios hilos.
     */
    public static ConexionSingleton getInstancia() {
        if (instancia == null) {
            synchronized (ConexionSingleton.class) {
                if (instancia == null) {
                    instancia = new ConexionSingleton();
                }
            }
        }
        return instancia;
    }

    /**
     * Singleton: abre la conexión una sola vez y reutiliza una conexión activa.
     *
     * @return conexión a la base de datos Municipalidad
     * @throws SQLException si faltan credenciales o SQL Server no está disponible
     */
    public synchronized Connection getConexion() throws SQLException {
        validarCredenciales();

        if (conexion == null || conexion.isClosed()) {
            conexion = DriverManager.getConnection(url, usuario, clave);
        }
        return conexion;
    }

    /**
     * Singleton: cierra la conexión compartida al finalizar la aplicación.
     *
     * @throws SQLException si SQL Server informa un error al cerrar
     */
    public synchronized void cerrarConexion() throws SQLException {
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
    }

    /**
     * Singleton: lee el archivo config.properties (UTF-8) si existe en la
     * carpeta de trabajo, para no escribir credenciales en el código.
     */
    private void cargarArchivo() {
        Path ruta = Paths.get(ARCHIVO_CONFIG);
        if (Files.exists(ruta)) {
            try (Reader lector = new InputStreamReader(
                    Files.newInputStream(ruta), StandardCharsets.UTF_8)) {
                archivo.load(lector);
            } catch (IOException e) {
                System.err.println("No se pudo leer " + ARCHIVO_CONFIG
                        + ": " + e.getMessage());
            }
        }
    }

    /**
     * Singleton: busca un valor en este orden: propiedad del sistema,
     * variable de entorno, config.properties y valor predeterminado.
     */
    private String obtenerConfiguracion(String nombre, String valorPorDefecto) {
        String valor = System.getProperty(nombre);
        if (valor == null || valor.trim().isEmpty()) {
            valor = System.getenv(nombre);
        }
        if (valor == null || valor.trim().isEmpty()) {
            valor = archivo.getProperty(nombre);
        }
        if (valor == null || valor.trim().isEmpty()) {
            return valorPorDefecto;
        }
        return valor.trim();
    }

    /**
     * Singleton: evita intentar una conexión silenciosa sin usuario o clave.
     *
     * @throws SQLException si no se configuraron las credenciales
     */
    private void validarCredenciales() throws SQLException {
        if (usuario.isEmpty() || clave.isEmpty()) {
            throw new SQLException(
                    "Configure DB_USER y DB_PASSWORD en config.properties "
                            + "o como variables de entorno.");
        }
    }
}