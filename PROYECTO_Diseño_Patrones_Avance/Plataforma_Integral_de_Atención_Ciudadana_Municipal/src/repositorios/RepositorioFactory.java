package repositorios;

import patrones.creacionales.singleton.ConexionSingleton;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Repositorio: factoria estatica que elige entre SQL Server y memoria
 * segun disponibilidad de la base de datos.
 * Factory Method: encapsula la logica de creacion del repositorio correcto.
 */
public class RepositorioFactory {

    private RepositorioFactory() {}

    /**
     * Factory Method: devuelve TramiteRepositorioSqlServer si la conexion Singleton
     * responde en 2 segundos; de lo contrario devuelve TramiteRepositorioMemoria.
     */
    public static TramiteRepositorio crear() {
        try {
            Connection conn = ConexionSingleton.getInstancia().getConexion();
            if (conn != null && conn.isValid(2)) {
                return new TramiteRepositorioSqlServer();
            }
        } catch (SQLException ex) {
            System.out.println("BD no disponible: usando repositorio en memoria");
        }
        return new TramiteRepositorioMemoria();
    }
}