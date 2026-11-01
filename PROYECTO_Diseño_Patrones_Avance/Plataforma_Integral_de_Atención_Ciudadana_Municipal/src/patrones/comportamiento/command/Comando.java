package patrones.comportamiento.command;

/**
 * Patrón Command: contrato que encapsula una solicitud u operación como un objeto,
 * permitiendo parametrizar invocadores, encolar operaciones y proporcionar
 * soporte uniforme para operaciones reversibles (deshacer/undo).
 */
public interface Comando {

    /**
     * Patrón Command: ejecuta la operación encapsulada.
     */
    void ejecutar();

    /**
     * Patrón Command: revierte la operación realizada durante la ejecución.
     */
    void deshacer();

    /**
     * Patrón Command: retorna una descripción legible de la acción.
     *
     * @return descripción de la acción para trazas e historial
     */
    String getDescripcion();
}

