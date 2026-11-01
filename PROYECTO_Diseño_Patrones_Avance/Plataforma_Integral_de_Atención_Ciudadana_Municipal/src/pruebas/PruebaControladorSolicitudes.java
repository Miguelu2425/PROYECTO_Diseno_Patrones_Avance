package pruebas;

import controladores.AtencionController;
import patrones.creacionales.builder.Expediente;
import patrones.estructurales.facade.MesaPartesFacade;
import repositorios.TramiteRepositorioMemoria;

import java.util.List;

/**
 * Prueba unitaria sin interfaz gráfica para validar los métodos agregados en AtencionController
 * para el soporte de la vista (descripción, siguiente estado, situación, listados y avances).
 * Opera completamente con el repositorio en memoria mediante inyección de dependencias.
 */
public class PruebaControladorSolicitudes {

    private static int ok = 0;
    private static int fallo = 0;

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println(" INICIANDO PRUEBAS DE ATENCIONCONTROLLER (SIN GUI)");
        System.out.println("======================================================================");

        // Inicializar controlador con repositorio en memoria aislado
        TramiteRepositorioMemoria repoMemoria = new TramiteRepositorioMemoria();
        MesaPartesFacade facade = new MesaPartesFacade(repoMemoria);
        AtencionController controller = new AtencionController(facade);

        // 1. Verificación de helpers de estado para vistas
        probarMetodosDeEstado(controller);

        // 2. Verificación de listados con y sin filtro DNI
        probarListadoSolicitudes(controller);

        // 3. Verificación del avance de estados y condición terminal
        probarCicloAvanceEstados(controller);

        // 4. Verificación de prueba de conexión Singleton
        probarMetodoConexion(controller);

        System.out.println("\n=== RESULTADO PRUEBA CONTROLADOR: " + ok + " OK, " + fallo + " FALLO ===");
        if (fallo > 0) {
            System.exit(1);
        }
    }

    private static void probarMetodosDeEstado(AtencionController c) {
        System.out.println("\n--- [1] Métodos de descripción, siguiente estado y situación ---");

        // descripcionEstado
        verificar("descripcionEstado('En Revision') describe espera",
                c.descripcionEstado("En Revision").toLowerCase().contains("espera"));
        verificar("descripcionEstado('Atendido') describe atencion municipal",
                c.descripcionEstado("Atendido").toLowerCase().contains("atendido"));
        verificar("descripcionEstado('Cerrado') describe tramite finalizado",
                c.descripcionEstado("Cerrado").toLowerCase().contains("finalizado"));

        // siguienteEstado
        verificar("siguienteEstado('En Revision') es 'Atendido'",
                "Atendido".equals(c.siguienteEstado("En Revision")));
        verificar("siguienteEstado('Atendido') es 'Cerrado'",
                "Cerrado".equals(c.siguienteEstado("Atendido")));
        verificar("siguienteEstado('Cerrado') es '-' (terminal)",
                "-".equals(c.siguienteEstado("Cerrado")));

        // situacionEstado
        verificar("situacionEstado('En Revision') es 'En espera'",
                "En espera".equals(c.situacionEstado("En Revision")));
        verificar("situacionEstado('Atendido') es 'Atendido'",
                "Atendido".equals(c.situacionEstado("Atendido")));
        verificar("situacionEstado('Cerrado') es 'Finalizado'",
                "Finalizado".equals(c.situacionEstado("Cerrado")));

        // nombreMunicipio
        verificar("nombreMunicipio() devuelve texto no vacío",
                c.nombreMunicipio() != null && !c.nombreMunicipio().trim().isEmpty());
    }

    private static void probarListadoSolicitudes(AtencionController c) {
        System.out.println("\n--- [2] Registro y listado de solicitudes ---");

        Expediente e1 = c.atenderCiudadano("11223344", "Carlos Lopez", "carlos@test.com", "solicitud");
        Expediente e2 = c.atenderCiudadano("55667788", "Maria Santos", "maria@test.com", "reclamo");

        verificar("Registro 1 exitoso", e1 != null && e1.getCodigo().startsWith("EXP-"));
        verificar("Registro 2 exitoso", e2 != null && e2.getCodigo().startsWith("EXP-"));

        // Listar todos (DNI = null)
        List<Expediente> todos = c.listarMisSolicitudes(null);
        verificar("listarMisSolicitudes(null) devuelve al menos 2 trámites", todos.size() >= 2);

        // Listar filtrado por DNI existente
        List<Expediente> soloCarlos = c.listarMisSolicitudes("11223344");
        verificar("listarMisSolicitudes('11223344') devuelve exactamente 1 trámite", soloCarlos.size() == 1);
        verificar("DNI del trámite listado coincide", "11223344".equals(soloCarlos.get(0).getDni()));

        // Listar filtrado por DNI inexistente
        List<Expediente> vacio = c.listarMisSolicitudes("00000000");
        verificar("listarMisSolicitudes con DNI inexistente devuelve lista vacía", vacio.isEmpty());
    }

    private static void probarCicloAvanceEstados(AtencionController c) {
        System.out.println("\n--- [3] Ciclo completo de avance de estado ---");

        Expediente exp = c.atenderCiudadano("99887766", "Raul Castro", "raul@test.com", "solicitud");
        verificar("Estado inicial es 'En Revision'", "En Revision".equals(exp.getEstado()));

        // Primer avance: En Revision -> Atendido
        Expediente expAtendido = c.avanzarEstado(exp.getCodigo());
        verificar("Primer avance lleva a 'Atendido'", "Atendido".equals(expAtendido.getEstado()));

        // Segundo avance: Atendido -> Cerrado
        Expediente expCerrado = c.avanzarEstado(exp.getCodigo());
        verificar("Segundo avance lleva a 'Cerrado'", "Cerrado".equals(expCerrado.getEstado()));

        // Tercer avance desde Cerrado (estado terminal)
        Expediente expTercero = c.avanzarEstado(exp.getCodigo());
        verificar("Tercer avance mantiene estado 'Cerrado'", "Cerrado".equals(expTercero.getEstado()));
    }

    private static void probarMetodoConexion(AtencionController c) {
        System.out.println("\n--- [4] Método probarConexion() ---");
        String resultado = c.probarConexion();
        verificar("probarConexion() devuelve respuesta descriptiva",
                resultado != null && (resultado.contains("BD conectada") || resultado.contains("modo memoria")));
    }

    private static void verificar(String descripcion, boolean condicion) {
        if (condicion) {
            System.out.println("[OK]    " + descripcion);
            ok++;
        } else {
            System.out.println("[FALLO] " + descripcion);
            fallo++;
        }
    }
}
