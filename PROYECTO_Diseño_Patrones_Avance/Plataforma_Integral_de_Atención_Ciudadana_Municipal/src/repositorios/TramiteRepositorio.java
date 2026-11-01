package repositorios;

import modelos.Ciudadano;
import patrones.creacionales.builder.Expediente;
import java.util.List;

/**
 * Repositorio: contrato que abstrae el mecanismo de persistencia de tramites.
 * GRASP Polimorfismo: permite intercambiar SQL Server y memoria sin cambiar clientes.
 */
public interface TramiteRepositorio {

    /**
     * Repositorio: registra un nuevo tramite y devuelve el expediente creado.
     *
     * @param ciudadano   ciudadano solicitante
     * @param tipoTramite tipo de tramite (ej. "Reclamo" o "Solicitud")
     * @return expediente con codigo unico asignado
     */
    Expediente registrar(Ciudadano ciudadano, String tipoTramite);

    /**
     * Repositorio: cambia el estado de un expediente identificado por su codigo.
     *
     * @param codigo      codigo del expediente (ej. EXP-2026-0001)
     * @param estadoNuevo nuevo estado a asignar
     * @return expediente actualizado
     */
    Expediente cambiarEstado(String codigo, String estadoNuevo);

    /**
     * Repositorio: lista expedientes; filtra por DNI solo si dniOpcional no es null.
     *
     * @param dniOpcional DNI del ciudadano o null para listar todos
     * @return lista ordenada por fecha de registro descendente
     */
    List<Expediente> listar(String dniOpcional);

    /**
     * Repositorio: describe el origen del repositorio activo.
     *
     * @return texto descriptivo del modo de persistencia
     */
    String descripcionOrigen();
}