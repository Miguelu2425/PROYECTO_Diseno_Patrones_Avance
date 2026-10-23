package patrones.estructurales.adapter;

import patrones.creacionales.productos.Tramite;

/**
 * Adapter Adaptee: sistema heredado que conserva su operación original.
 */
class SistemaAntiguoQuejas {

    /**
     * Adaptee: registra los datos usando la interfaz del sistema heredado.
     */
    public void registrarQuejaVecinal(String datos) {
        System.out.println("Registro en sistema antiguo: " + datos);
    }
}

/**
 * Adapter: adapta el sistema antiguo al contrato Tramite del portal actual.
 */
public class AdaptadorSistemaAntiguo implements Tramite {

    private SistemaAntiguoQuejas sistemaViejo;

    /**
     * Adapter: crea el adaptee heredado para facilitar su uso desde el portal.
     */
    public AdaptadorSistemaAntiguo() {
        this(new SistemaAntiguoQuejas());
    }

    /**
     * Adapter: recibe explícitamente el sistema heredado que será adaptado.
     */
    public AdaptadorSistemaAntiguo(SistemaAntiguoQuejas sistemaViejo) {
        this.sistemaViejo = sistemaViejo;
    }

    @Override
    /**
     * Adapter: traduce la operación moderna al método del sistema antiguo.
     */
    public void procesar() {
        sistemaViejo.registrarQuejaVecinal("Datos adaptados al nuevo portal");
    }
}
