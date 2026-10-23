package patrones.estructurales.composite;

import patrones.creacionales.productos.Tramite;
import java.util.ArrayList;
import java.util.List;

/**
 * Composite: trata trámites individuales y grupos mediante el mismo contrato.
 */
public class GrupoTramites implements Tramite {

    private List<Tramite> tramites = new ArrayList<>();

    /**
     * Composite: agrega un componente hijo al grupo de trámites.
     */
    public void agregarTramite(Tramite t) {
        tramites.add(t);
    }

    @Override
    /**
     * Composite: procesa todos los componentes hijos de forma uniforme.
     */
    public void procesar() {
        System.out.println("Procesando grupo de tramites vinculados...");
        for (Tramite t : tramites) {
            t.procesar();
        }
    }
}
