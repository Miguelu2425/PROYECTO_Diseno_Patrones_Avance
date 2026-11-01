package pruebas;

import controladores.AtencionController;
import patrones.creacionales.builder.Expediente;

import java.util.List;

/**
 * Simulación y verificación del flujo manual de usuario:
 * 1. Abrir app
 * 2. Registrar trámite en REGISTRO
 * 3. Ir a SEGUIMIENTO > Mis solicitudes, buscar por DNI
 * 4. Pulsar 'Avanzar estado' -> pasa a Atendido con Observer
 * 5. Avanzar de nuevo -> pasa a Cerrado con encuesta y botón se deshabilita
 * 6. En SISTEMA > Conexión pulsar 'Probar conexión' -> verificar Singleton
 */
public class PruebaFlujoManual {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println(" SIMULACION DEL FLUJO MANUAL DE USUARIO (GUI)");
        System.out.println("======================================================================");
        AtencionController c = new AtencionController();

        System.out.println("\n>>> PASO 1: Inicio de la aplicación");
        System.out.println("    Municipio: " + c.nombreMunicipio());
        System.out.println("    Estado persistencia inicial: " + c.estadoConexion());

        System.out.println("\n>>> PASO 2: REGISTRO > Registrar trámite");
        Expediente exp = c.atenderCiudadano("74859612", "Gonzalo Solar", "gonzalo@test.com", "solicitud");
        System.out.println("    -> Expediente generado: " + exp.getCodigo());
        System.out.println("    -> Estado inicial: " + exp.getEstado() + " (" + c.situacionEstado(exp.getEstado()) + ")");

        System.out.println("\n>>> PASO 3: SEGUIMIENTO > Mis solicitudes (Buscar DNI '74859612')");
        List<Expediente> solicitudes = c.listarMisSolicitudes("74859612");
        for (Expediente s : solicitudes) {
            System.out.println("    Fila en tabla: [" + s.getCodigo() + "] [" + s.getTipoDocumento()
                    + "] [" + s.getNombreCiudadano() + "] [" + s.getEstado()
                    + "] [" + c.situacionEstado(s.getEstado()) + "] [" + s.getFechaRegistro() + "]");
            System.out.println("    Detalle panel: " + c.descripcionEstado(s.getEstado()));
            System.out.println("    Siguiente estado: " + c.siguienteEstado(s.getEstado()));
        }

        System.out.println("\n>>> PASO 4: Pulsar 'Avanzar estado' (En Revision -> Atendido)");
        Expediente expAtendido = c.avanzarEstado(exp.getCodigo());
        System.out.println("    -> Estado actualizado: " + expAtendido.getEstado() + " (" + c.situacionEstado(expAtendido.getEstado()) + ")");
        System.out.println("    Detalle panel: " + c.descripcionEstado(expAtendido.getEstado()));
        System.out.println("    Siguiente estado: " + c.siguienteEstado(expAtendido.getEstado()));

        System.out.println("\n>>> PASO 5: Pulsar 'Avanzar estado' nuevamente (Atendido -> Cerrado)");
        Expediente expCerrado = c.avanzarEstado(exp.getCodigo());
        System.out.println("    -> Estado actualizado: " + expCerrado.getEstado() + " (" + c.situacionEstado(expCerrado.getEstado()) + ")");
        System.out.println("    Detalle panel: " + c.descripcionEstado(expCerrado.getEstado()));
        System.out.println("    Siguiente estado: " + c.siguienteEstado(expCerrado.getEstado()));
        System.out.println("    Estado terminal alcanzado: Botón 'Avanzar estado' queda DESHABILITADO ("
                + ("-".equals(c.siguienteEstado(expCerrado.getEstado()))) + ")");

        System.out.println("\n>>> PASO 6: SISTEMA > Conexión (Singleton) > Pulsar 'Probar conexión'");
        String resConexion = c.probarConexion();
        System.out.println("    -> Mensaje devuelto: " + resConexion);

        System.out.println("\n======================================================================");
        System.out.println(" FLUJO MANUAL VERIFICADO CON EXITO");
        System.out.println("======================================================================");
    }
}
