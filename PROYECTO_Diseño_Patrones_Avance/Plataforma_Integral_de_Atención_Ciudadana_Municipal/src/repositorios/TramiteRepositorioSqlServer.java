package repositorios;

import modelos.Ciudadano;
import patrones.creacionales.builder.Expediente;
import patrones.creacionales.singleton.ConexionSingleton;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositorio: implementacion SQL Server que invoca los stored procedures
 * via ConexionSingleton. NO cierra la Connection porque es compartida.
 * Builder: usa ExpedienteBuilder para construir cada Expediente del ResultSet.
 */
public class TramiteRepositorioSqlServer implements TramiteRepositorio {

    @Override
    public Expediente registrar(Ciudadano ciudadano, String tipoTramite) {
        String sql = "{call dbo.sp_RegistrarTramite(?,?,?,?)}";
        Connection conn = obtenerConexion();
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, ciudadano.getDni());
            cs.setString(2, ciudadano.getNombre());
            cs.setString(3, ciudadano.getCorreo());
            cs.setString(4, capitalizar(tipoTramite));
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return construirExpediente(rs);
                }
            }
            throw new RepositorioException("sp_RegistrarTramite no devolvio filas.");
        } catch (SQLException ex) {
            throw new RepositorioException("Error al registrar tramite: " + ex.getMessage(), ex);
        }
    }

    @Override
    public Expediente cambiarEstado(String codigo, String estadoNuevo) {
        String sql = "{call dbo.sp_CambiarEstado(?,?)}";
        Connection conn = obtenerConexion();
        try (CallableStatement cs = conn.prepareCall(sql)) {
            cs.setString(1, codigo);
            cs.setString(2, estadoNuevo);
            try (ResultSet rs = cs.executeQuery()) {
                if (rs.next()) {
                    return construirExpediente(rs);
                }
            }
            throw new RepositorioException("sp_CambiarEstado no devolvio filas para: " + codigo);
        } catch (SQLException ex) {
            throw new RepositorioException("Error al cambiar estado: " + ex.getMessage(), ex);
        }
    }

    @Override
    public List<Expediente> listar(String dniOpcional) {
        String sql = "{call dbo.sp_ListarTramites(?)}";
        Connection conn = obtenerConexion();
        List<Expediente> lista = new ArrayList<>();
        try (CallableStatement cs = conn.prepareCall(sql)) {
            if (dniOpcional != null) {
                cs.setString(1, dniOpcional);
            } else {
                cs.setNull(1, java.sql.Types.CHAR);
            }
            try (ResultSet rs = cs.executeQuery()) {
                while (rs.next()) {
                    lista.add(construirExpediente(rs));
                }
            }
        } catch (SQLException ex) {
            throw new RepositorioException("Error al listar tramites: " + ex.getMessage(), ex);
        }
        return lista;
    }

    @Override
    public String descripcionOrigen() {
        return "conectado a SQL Server mediante ConexionSingleton";
    }

    private Connection obtenerConexion() {
        try {
            return ConexionSingleton.getInstancia().getConexion();
        } catch (SQLException ex) {
            throw new RepositorioException("No se pudo obtener conexion Singleton: " + ex.getMessage(), ex);
        }
    }

    /**
     * Builder: construye un Expediente leyendo las columnas estandar del ResultSet.
     * Columnas esperadas: codigo, dni, ciudadano (nombre), tipo_tramite, estado, fecha_registro.
     */
    private Expediente construirExpediente(ResultSet rs) throws SQLException {
        return new Expediente.ExpedienteBuilder()
                .setCodigo(rs.getString("codigo"))
                .setEstado(rs.getString("estado"))
                .setTipoDocumento(rs.getString("tipo_tramite"))
                .setDni(rs.getString("dni"))
                .setNombreCiudadano(rs.getString("ciudadano"))
                .setFechaRegistro(rs.getString("fecha_registro"))
                .build();
    }

    private String capitalizar(String texto) {
        if (texto == null || texto.isEmpty()) return texto;
        return Character.toUpperCase(texto.charAt(0)) + texto.substring(1).toLowerCase();
    }
}